package com.assessory.api

import com.assessory.api.wiring.Lookups
import com.wbillingsley.handy._
import Ref._
import course._
import group._
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
    hasRole(course, prior.who, CourseRole.student, prior.cache)
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
      a <- prior ask ViewCourse(g.course)
    } yield Approved("Course viewers can view groups")
  }

  val EditGroup = Perm.onId[User, Group, String] { case (prior, rGroup) =>
    for {
      g <- rGroup
      a <- prior ask EditCourse(g.course)
    } yield Approved("Course editors can edit groups")
  }

  val EditCourse = Perm.onId[User, Course, String] { case (prior, rCourse) =>
    hasRole(rCourse, prior.who, CourseRole.staff, prior.cache)
  }  

  val ViewTask = Perm.onId[User, Task, String] { case (prior, task) =>
    for (
        t <- task;
        a <- prior ask ViewCourse(t.course)
    ) yield a
  }  
  
  val EditTask = Perm.onId[User, Task, String] { case (prior, task) =>
    for (
        t <- task;
        a <- prior ask EditCourse(t.course)
    ) yield a
  }  

  val EditOutput = Perm.onId[User, TaskOutput, String] { case (prior, to) =>
    for {
      o <- to
      byId <- o.byUser.refId
      whoId <- prior.who.refId
      task <- o.task
      result <- {
        if (byId != whoId) {
          RefFailed(Refused("You may only edit your own work"))
        } else {
          val overdue = for {
            due <- task.details.due if (due < System.currentTimeMillis())
          } yield RefFailed(Refused("You may only edit work before it is due"))
          overdue.getOrElse(Approved("You may edit this").itself)
        }
      }
    } yield result
  }

  val WriteCritique = Perm.onId[User, CritAllocation, String] { case (prior, gca) =>
    (
      for {
        g <- gca
        uId <- g.user.refId
        whoId <- prior.who.refId if (uId == whoId)
      } yield Approved("You may write critiques that are allocated to you")
    ) orIfNone Refused("You may only write crtitiques that are allocated to you")
  }

  def getRoles(course: Ref[Course], user: Ref[User]) = {
    for {
       uId <- user.refId
       cId <- course.refId
       r <- Lookups.registrationProvider.byUserAndCourse(uId, cId)
    } yield r.roles
  }
  
  def hasRole(course:Ref[Course], user:Ref[User], role:CourseRole.T, cache:LookUpCache):Ref[Approved] = {
    (
      for (
        roles <- getRoles(course, user) if roles.contains(role)
      ) yield Approved(s"You have role $role for this course")
    ) orIfNone Refused(s"You do not have role $role for this course")
  }  
}