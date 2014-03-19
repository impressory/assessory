package com.assessory.api

import com.wbillingsley.handy._
import course._
import group._
import com.assessory.api.groupcrit.GroupCritAllocation

import wiring.Lookups._
import com.wbillingsley.handy.HasStringId.GetsStringId

object Permissions {

  
  
  /**
   * Create a course
   */
  case object CreateCourse extends Perm[User] {    
    def resolve(prior:Approval[User]) = {
      Approved("Anyone may create a course")
    }
  }

  case class ViewCourse(course:Ref[Course]) extends PermOnIdRef[User, Course](course) {
    def resolve(prior:Approval[User]) = hasRole(course, prior.who, CourseRole.student, prior.cache)
  }

  val ViewGroupSet = Perm.cacheOnId[User, GroupSet] { case (prior, rGroupSet) =>
    for {
      gs <- rGroupSet
      a <- prior ask ViewCourse(gs.course)
    } yield Approved("Course viewers can view group sets")
  }

  val EditGroupSet = Perm.cacheOnId[User, GroupSet] { case (prior, rGroupSet) =>
    for {
      gs <- rGroupSet
      a <- prior ask EditCourse(gs.course)
    } yield Approved("Course editors can edit group sets")
  }

  val ViewGroup = Perm.cacheOnId[User, Group] { case (prior, rGroup) =>
    for {
      g <- rGroup
      a <- prior ask ViewCourse(g.course)
    } yield Approved("Course viewers can view groups")
  }

  val EditGroup = Perm.cacheOnId[User, Group] { case (prior, rGroup) =>
    for {
      g <- rGroup
      a <- prior ask EditCourse(g.course)
    } yield Approved("Course editors can edit groups")
  }

  case class EditCourse(course:Ref[Course]) extends PermOnIdRef[User, Course](course) {
    def resolve(prior:Approval[User]) = hasRole(course, prior.who, CourseRole.staff, prior.cache)
  }  

  case class ViewTask(task:Ref[Task]) extends PermOnIdRef[User, Task](task) {
    def resolve(prior:Approval[User]) = for (
        t <- task;
        a <- prior ask ViewCourse(t.course)
    ) yield a
  }  
  
  case class EditTask(task:Ref[Task]) extends PermOnIdRef[User, Task](task) {
    def resolve(prior:Approval[User]) = for (
        t <- task;
        a <- prior ask EditCourse(t.course)
    ) yield a
  }  

  case class EditOutput(to:Ref[TaskOutput]) extends PermOnIdRef[User, TaskOutput](to) {
    def resolve(prior:Approval[User]) = {
      (for {
        o <- to
        byId <- o.byUser.refId
        whoId <- prior.who.refId if (!o.finalised.isDefined && byId == whoId)
      } yield Approved("You may write edit your own output")) orIfNone Refused("You may only edit your own critiques, before they have been finalised")
    }
  }  
  
  case class WriteCritique(gca:Ref[GroupCritAllocation]) extends PermOnIdRef[User, GroupCritAllocation](gca) {
    def resolve(prior:Approval[User]) = {
      (
        for {
          g <- gca
          uId <- g.user.refId
          whoId <- prior.who.refId if (uId == whoId)
        } yield Approved("You may write critiques that are allocated to you")
      ) orIfNone Refused("You may only write crtitiques that are allocated to you")
    }
  }  
  
  def getRoles(course: Ref[Course], user: Ref[User]) = {
    for {
       u <- user
       cId <- course.refId
       r <- u.registrations.find(_.course.getId == Some(cId))
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