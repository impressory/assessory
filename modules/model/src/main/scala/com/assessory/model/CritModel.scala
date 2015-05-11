package com.assessory.model

import java.io.StringWriter

import au.com.bytecode.opencsv.CSVWriter
import com.assessory.api._
import com.assessory.api.critique._
import com.assessory.api.wiring.Lookups._
import com.assessory.asyncmongo._
import com.wbillingsley.handy.Id._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase._

object CritModel {


  /**
   * Allocates within these groups, taking no account of groups' parents
   * @param groups
   * @param num
   * @return
   */
  def allocateTheseGroups(inGroups:Seq[Group], inRegs:Seq[Group.Reg], num:Int) = {
    import scala.collection.mutable

    val groups = inGroups.sortBy(_.name)
    val regs = inRegs.sortBy(_.user.id)

    println("ALLOCATING " + groups.map(_.name) + s"for ${regs.size} students")

    val groupIds = groups.map(_.id)
    val memberMap = for {
      (g,r) <- regs.groupBy(_.target)
    } yield g -> r.map(_.user).toSet

    val reverseMap = (for (g <- groupIds) yield g -> mutable.Set.empty[Id[User,String]]).toMap
    val forwardMap = (for (r <- regs; u = r.user) yield u -> mutable.Set.empty[Id[Group,String]]).toMap

    def pick(u:Id[User,String]) = {
      val g = groupIds
      val filtered = groupIds.filterNot(g => memberMap(g).contains(u) || reverseMap(g).contains(u))
      try {
        filtered.minBy(reverseMap(_).size)
      } catch {
        case x:Throwable =>
          println("ERRORED FOR" + groups.map(_.name) + s"for ${regs.size} students")
          throw x
      }

    }

    scala.util.Random.setSeed(2015)
    val shuffled = regs

    for {
      i <- 1 to num
      reg <- shuffled
    } {
      val g = pick(reg.user)
      reverseMap(g).add(reg.user)
      forwardMap(reg.user).add(g)
    }

    forwardMap
  }

  def allocateGroups(inGroups:Seq[Group], num:Int, task:Id[Task,String]):RefMany[CritAllocation] = {
    for {
      (rParent, groups) <- inGroups.groupBy(_.parent).toRefMany
      groupIds = groups.map(_.id)
      registrations <- RegistrationDAO.group.byTargets(groupIds).collect

      critMap = allocateTheseGroups(groups, registrations, num)

      (u, gIds) <- critMap.toRefMany

      unsaved = CritAllocation(
        id = CritAllocationDAO.allocateId.asId,
        task = task,
        completeBy = TargetUser(u),
        allocation = for (g <- gIds.toSeq) yield AllocatedCrit(target=TargetGroup(g))
      )

      saved <- CritAllocationDAO.saveNew(unsaved)
    } yield saved
  }


  def allocateTask(a:Approval[User], rTask:RefWithId[Task]):RefMany[CritAllocation] = {
    for {
      t <- rTask
      approved <- a ask Permissions.EditCourse(t.course)
      body <- t.body match {
        case ct:CritiqueTask => ct.itself
        case _ => RefFailed(UserError("I can only allocate crit tasks"))
      }
      strategy <- body.strategy match {
        case p:PreallocateGroupStrategy => p.itself
        case _ => RefFailed(UserError("I can only allocate group crit tasks"))
      }
      groups <- GroupDAO.bySet(strategy.set).toRefOne
      alloc <- allocateGroups(groups.toSeq, strategy.number, t.id)
    } yield alloc
  }


  def allocations(rTask:RefWithId[Task]) = {
    for (t <- CritAllocationDAO.byTask(rTask)) yield t
  }

  private def allocationsFor(a:Approval[User], task:Task):RefMany[Target] = {
    task.body match {
      case CritiqueTask(qs, strategy) =>
        strategy match {
          case PreallocateGroupStrategy(set, num) =>
            for {
              u <- a.who
              alloc <- CritAllocationDAO.byUserAndTask(u.itself, task.itself)
              ac <- alloc.allocation.toRefMany
            } yield ac.target
          case OfMyGroupsStrategy =>
            for {
              group <- GroupModel.myGroups(a, a.cache(task.course.lazily)) if Some(group.set) == task.details.groupSet
              to <- TaskOutputDAO.byTaskAndAttn(task.itself, TargetGroup(group.id))
            } yield TargetTaskOutput(to.id)
        }
    }
  }

  def myAllocations(a:Approval[User], rTask:RefWithId[Task]):RefMany[Target] = {
    for {
      u <- a.who
      t <- rTask
      ct <- t.body match {
        case ct:CritiqueTask => ct.itself
        case _ => RefFailed(UserError("This was not a critique task"))
      }
      target <- allocationsFor(a, t)
    } yield target
  }


  def byFromTask(a:Approval[User], t:Task):Ref[Target] = {

    // Get the user's groups in the corresponding groupSet, if it's not an individual assignment
    val groups = for {
      u <- a.who
      gs <- a.cache(t.details.groupSet.lazily) if !t.details.individual
      g <- GroupModel.myGroups(a, t.course.lazily) if g.set == gs.id
    } yield g

    // Pick the first group
    val targetGroup = for {
      u <- a.who
      gs <- groups.collect
      g <- gs.headOption
    } yield TargetGroup(g.id)

    // Return the group, or the user if there isn't one
    for {
      u <- a.who
      t <- targetGroup orIfNone TargetUser(u.id).itself
    } yield t
  }


  private def blankAnswer(q:Question) = q match {
    case ShortTextQuestion(id, _, _) => ShortTextAnswer(id, None)
    case BooleanQuestion(id, _) => BooleanAnswer(id, None)
  }

  /**
   * Creates a new TaskOutput for a critique, with blank answers
   * @param by
   * @param task
   * @param target
   * @return
   */
  private def createCrit(by:Target, task:Task, target:Target):Ref[TaskOutput] = {
    task.body match {
      case ct:CritiqueTask =>
        val unsaved = TaskOutput(
          id = TaskOutputDAO.allocateId.asId,
          by = by,
          attn = Seq(target),
          task = task.id,
          body = Critique(
            target = target,
            answers = for {
              q <- ct.questionnaire
            } yield blankAnswer(q)
          )
        )
        TaskOutputDAO.saveSafe(unsaved)
      case _ =>
        RefFailed(UserError("I can only create critiques for critique tasks"))
    }
  }

  /**
   * Looks up a user's TaskOutput for a critique, or creates a blank one if there isn't one
   * @param a
   * @param rTask
   * @param target
   * @return
   */
  def findOrCreateCrit(a:Approval[User], rTask:Ref[Task], target:Target) = {
    for {
      u <- a.who
      t <- rTask
      completeBy <- byFromTask(a, t)
      taskOutputs <- TaskOutputDAO.byTaskAndBy(t.id, completeBy).withFilter(
        _.body match {
          case Critique(storedTarget, _) if storedTarget == target => true
          case _ => false
        }
      ).collect
      to <- Ref(taskOutputs.headOption) orIfNone createCrit(completeBy, t, target)
      wp <- TaskOutputModel.withPerms(a, to)
    } yield wp
  }

  def findCritForAlloc(a:Approval[User], rca:Ref[CritAllocation], target:Target) = {
    for {
      u <- a.who
      ca <- rca
      alloc <- ca.allocation.find(_.target == target).toRef orIfNone UserError("This allocation doesn't include that target")
      crit <- alloc.critique.lazily orIfNone (for {
        wp <- findOrCreateCrit(a, a.cache.lookUp(ca.task), target)
        updatedAlloc <- CritAllocationDAO.setOutput(ca.id, target, wp.item.id)
      } yield wp.item)
    } yield crit
  }


  private def targString(a:Approval[User], t:Target) = {
    t match {
      case TargetUser(id) =>
        for {
          u <- a.cache.lookUp(id)
         id <- u.identities.find(_.service == I_STUDENT_NUMBER).flatMap(_.value)
        } yield Seq(id, u.name.getOrElse(""))
      case TargetGroup(id) =>
        for {
          g <- a.cache.lookUp(id)
        } yield Seq(g.name.getOrElse(""))
      case _ => RefFailed(UserError("Can't represent this target as a string"))
    }
  }

  /** Fetches allocations as a CSV. */
  def allocationsAsCSV(a:Approval[User], rTask:Ref[Task]):Ref[String] = {
    val sWriter = new StringWriter()
    val writer = new CSVWriter(sWriter)

    val lineArrays = for {
      t <- rTask
      c <- a.cache.lookUp(t.course)
      approved <- a ask Permissions.EditCourse(c.itself)
      allocC <- CritAllocationDAO.byTask(t.itself).collect
      alloc <- {
        println(s"THERE ARE ${allocC.size} ALLOCATIONS")
        allocC.toRefMany
      }
      by <- targString(a, alloc.completeBy)
      allocLine <- alloc.allocation.toRefMany
      targ <- targString(a, allocLine.target)
    } yield (by ++ targ).toArray

    for { lines <- lineArrays.collect } yield {
      for { line <- lines } writer.writeNext(line)
      writer.close()
      sWriter.toString
    }
  }
}
