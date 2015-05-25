package com.assessory.api

import com.assessory.api.wiring.Lookups
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase._
import Ref._
import Ids._
import critique._

import wiring.Lookups._

object Permissions {



  /**
   * Create a course
   */
  val CreateCourse = Perm.unique[User] { case (prior) =>
      Approved("Anyone may create a course")
  }

  val ViewCourse = Perm.onId[User, Course, String] { case (prior, course) =>
    hasAnyRole(course, prior.who, CourseRole.roles, prior.cache)
  }

  val EditCourse = Perm.onId[User, Course, String] { case (prior, rCourse) =>
    hasRole(rCourse, prior.who, CourseRole.staff, prior.cache)
  }

  val ViewGroupSet = Perm.onId[User, GroupSet, String] { case (prior, rGroupSet) =>
    for {
      gs <- rGroupSet
      a <- prior ask ViewCourse(gs.course)
    } yield Approved("Course viewers can view group sets")
  }

  val EditGroupSet = Perm.onId[User, GroupSet, String] { case (prior, rGroupSet) =>
    for {
      gs <- rGroupSet
      a <- prior ask EditCourse(gs.course)
    } yield Approved("Course editors can edit group sets")
  }

  val ViewGroup = Perm.onId[User, Group, String] { case (prior, rGroup) =>
    for {
      g <- rGroup
      a <- prior ask ViewCourse(prior.cache(g.course.lookUp))
    } yield Approved("Course viewers can view groups")
  }

  val EditGroup = Perm.onId[User, Group, String] { case (prior, rGroup) =>
    for {
      g <- rGroup
      a <- prior ask EditCourse(prior.cache(g.course.lookUp))
    } yield Approved("Course editors can edit groups")
  }

  val ViewTask = Perm.onId[User, Task, String] { case (prior, task) =>
    for (
        t <- task;
        a <- prior ask ViewCourse(t.course)
    ) yield a
  }

  val CompleteTask = Perm.onId[User, Task, String] { case (prior, task) =>
    for (
      t <- task;
      a <- prior ask ViewTask(t.itself);
      due <- isOpen(prior, t).withFilter({b:Boolean => b}) orIfNone UserError("This task is closed")
    ) yield a
  }


  val EditTask = Perm.onId[User, Task, String] { case (prior, task) =>
    for (
        t <- task;
        a <- prior ask EditCourse(t.course)
    ) yield a
  }

  def isOwn(prior:Approval[User], who:User, t:Target) = {
    t match {
      case TargetUser(uid) => {
        if (uid != who.id) {
          RefFailed(Refused("You may only edit your own work"))
        } else {
          Approved("Own work").itself
        }
      }
      case TargetCourseReg(cregId) => (
        for (creg <- prior.cache(cregId.lazily) if creg.user == who.id) yield Approved("Own work")
      ) orIfNone Refused("You may only edit your own work")
      case TargetGroup(gid) => (
        for (r <- Lookups.groupRegistrationProvider.byUserAndTarget(who.id, gid)) yield Approved("Registered in group")
      ) orIfNone Refused("You may only edit your own work")
    }
  }

  val EditOutput = Perm.onId[User, TaskOutput, String] { case (prior, to) =>
    for {
      who <- prior.cache(prior.who)
      o <- to
      task <- prior.cache(o.task.lazily)
      ownWork <- isOwn(prior, who, o.by) orIfNone UserError("This is not your own work")
      due <- isOpen(prior, task).withFilter({b:Boolean => b}) orIfNone UserError("This task is closed")
    } yield Approved("You may edit this")
  }

  def isOpen(a:Approval[User], t:Task) = {
    for {
      uId <- a.who.refId
      groupIds <- Lookups.groupRegistrationProvider.byUser(uId).map(_.target).collect
    } yield {
      val now = System.currentTimeMillis()
      // TODO: should we have a margin?
      val margin = 300000L

      def after(d:Due) = d.due(groupIds.map(_.id).asIds) match {
        case Some(l) => now - l
        case _ => 0L
      }

      after(t.details.open) > 0 && after(t.details.closed) < margin
    }
  }

  val WriteCritique = Perm.onId[User, CritAllocation, String] { case (prior, gca) =>
      for {
        who <- prior.cache(prior.who)
        g <- gca
        ownWork <- isOwn(prior, who, g.completeBy)
      } yield Approved("You may write critiques that are allocated to you")
  }

  def getRoles(course: Ref[Course], user: Ref[User]) = {
    for {
       uId <- user.refId
       cId <- course.refId
       r <- Lookups.courseRegistrationProvider.byUserAndTarget(uId, cId)
    } yield r.roles
  }

  def hasRole(course:Ref[Course], user:Ref[User], role:CourseRole, cache:LookUpCache):Ref[Approved] = {
    (
      for (
        roles <- getRoles(course, user) if roles.contains(role)
      ) yield Approved(s"You have role $role for this course")
    ) orIfNone Refused(s"You do not have role $role for this course")
  }

  def hasAnyRole(course:Ref[Course], user:Ref[User], roles:Set[CourseRole], cache:LookUpCache):Ref[Approved] = {
    (
      for (
        savedRoles <- getRoles(course, user) if roles.intersect(savedRoles).nonEmpty
      ) yield Approved(s"You have any of these roles $roles for this course")
      ) orIfNone Refused(s"You do not have any of these roles $roles for this course")
  }
}
