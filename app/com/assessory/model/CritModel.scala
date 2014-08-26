package com.assessory.model

import play.api.mvc.{Action, Controller}
import com.assessory.reactivemongo._
import com.assessory.play.json._
import play.api.mvc.AnyContent
import com.assessory.api._
import course._
import group._
import critique._
import question._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.assessory.reactivemongo.CritAllocationDAO
import Ref._

object CritModel {


  def allocateGroups(groups:Seq[Group], num:Int, task:RefWithId[Task]):RefMany[CritAllocation] = {

    for {
      (rParent, groups) <- groups.groupBy(_.parent).toRefMany
      parent <- rParent
      alloc <- {
        val shuffled = scala.util.Random.shuffle(groups)
        var reverse = (for (g <- shuffled) yield g -> Set.empty[User]).toMap

        def pick(m: Map[Group, Set[User]], u: User) = {
          val notIn = m.keys.filter {
            // Exclude groups that this user is a member of, and groups they are already reviewing
            g => !g.members.getIds.contains(u.id) && !m(g).contains(u)
          }
          notIn.minBy {
            g => m(g).size
          }
        }

        // Allocation for this student
        for {
          student <- parent.members
          allocations = {
            for {
              i <- 1 to num
            } yield {
              val g = pick(reverse, student)
              reverse = reverse.updated(g, reverse(g) + student)
              AllocatedCrit(
                target = CTGroup(g.itself)
              )
            }
          }
          unsaved = CritAllocationDAO.unsaved.copy(
            task = task,
            user = student.itself,
            allocation = allocations
          )
          saved <- CritAllocationDAO.saveNew(unsaved)
        } yield saved
      }

    } yield alloc

  }


  def allocateTask(a:Approval[User], rTask:RefWithId[Task]):RefMany[CritAllocation] = {
    for {
      t <- rTask
      approved <- a ask Permissions.EditCourse(t.course)
      body <- t.body match {
        case Some(ct:CritiqueTask) => ct.itself
        case _ => RefFailed(UserError("I can only allocate crit tasks"))
      }
      strategy <- body.strategy match {
        case p:PreallocateGroupStrategy => p.itself
        case _ => RefFailed(UserError("I can only allocate group crit tasks"))
      }
      groups <- GroupDAO.bySet(strategy.set).toRefOne
      alloc <- allocateGroups(groups.toSeq, strategy.number, t.itself)
    } yield alloc
  }


  def allocations(rTask:RefWithId[Task]) = {
    for (t <- CritAllocationDAO.byTask(rTask)) yield t
  }

  def myAllocations(a:Approval[User], rTask:RefWithId[Task]):RefMany[CritTarget] = {
    for {
      u <- a.who
      t <- rTask
      ct <- t.body match {
        case Some(ct:CritiqueTask) => ct.itself
        case _ => RefFailed(UserError("This was not a critique task"))
      }
      target <- ct.strategy match {
        case psg:PreallocateGroupStrategy => for {
          alloc <- CritAllocationDAO.byUserAndTask(u.itself, t.itself)
          ac <- alloc.allocation.toRefMany
        } yield ac.target
        case mos:OfMyGroupsStrategy => {
          for {
            group <- GroupModel.myGroups(a, t.course)
            output <- {
              println(group)
              TaskOutputDAO.byPartialBody(mos.task, Critique(CTGroup(group.itself), Seq.empty), Seq("answers"))
            }
          } yield CTTaskOutput(output.itself)
        }
      }
    } yield target
  }


  def findOrCreateCrit(a:Approval[User], rTask:Ref[Task], target:CritTarget) = {
    for {
      u <- a.who
      t <- rTask
      ct <- t.body match {
        case Some(ct:CritiqueTask) => ct.itself
        case _ => RefFailed(UserError("This was not a critique task"))
      }
      travTo <- (TaskOutputDAO.byTaskAndUser(t.itself, u.itself).withFilter(
        _.body match {
          case Some(Critique(storedTarget, ans)) if (storedTarget == target) => true
          case _ => false
        }
      )).toRefOne
      to <- Ref(travTo.toSeq.headOption) orIfNone {
        val unsaved = TaskOutputDAO.unsaved.copy(
          task = t.itself,
          byUser = u.itself,
          body = Some(
            Critique(
              target = target,
              answers = for {
                q <- ct.questionnaire.questions
              } yield q.blankAnswer
            )
          )
        )
        TaskOutputDAO.saveNew(unsaved)
      }
    } yield to
  }


  def createBlankCrit(a:Approval[User], rca:Ref[CritAllocation], target:CritTarget) = {
    for {
      u <- a.who
      ca <- rca
      approved <- a ask Permissions.WriteCritique(ca.itself)
      task <- ca.task orIfNone UserError("This allocation has no task")
      ct <- task.body match {
        case Some(ct:CritiqueTask) => ct.itself
        case _ => RefFailed(UserError("This was not a critique task"))
      }
      ac <- Ref(ca.allocation.find(_.target == target)) orIfNone UserError("This allocation doesn't include that target")
      unsaved = TaskOutputDAO.unsaved.copy(
        task = ca.task,
        byUser = u.itself,
        body = Some(
          Critique(
            target = ac.target,
            answers = for {
              q <- ct.questionnaire.questions
            } yield q.blankAnswer
          )
        )
      )
      saved <- TaskOutputDAO.saveNew(unsaved)
      updatedAlloc <- CritAllocationDAO.setOutput(ca.itself, ac.target, saved.itself)
    } yield saved
  }


  /** Fetches allocations as a CSV. */
  def allocationsAsCSV(a:Approval[User], rTask:Ref[Task]):RefMany[String] = {
    for {
      t <- rTask;
      c <- t.course
      approved <- a ask Permissions.EditCourse(c.itself)
      alloc <- CritAllocationDAO.byTask(t.itself)
      user <- alloc.user
      studentIdent <- Ref(user.identities.find(_.service == c.id))
      studentNumber = studentIdent.value
      username <- Ref(user.nickname)
      lineData <- {
        for {
          allocation <- alloc.allocation.toRefMany
          targ <- allocation.target match {
            case CTTaskOutput(t) => {
              t.refId
            }
            case CTGroup(g) => {
              for {
                group <- g
              } yield group.name.getOrElse(group.id)
            }
          }
        } yield targ
      }.toRefOne
    } yield {
      lineData.toSeq.fold(s"${studentNumber},${username}")((sofar, gn) => sofar + ","+gn) + "\n"
    }
  }
}