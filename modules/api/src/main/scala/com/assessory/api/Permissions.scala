package com.assessory.api

import com.wbillingsley.handy._
import course._
import com.assessory.api.groupcrit.GroupCritAllocation

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
  
  case class EditCourse(course:Ref[Course]) extends PermOnIdRef[User, Course](course) {
    def resolve(prior:Approval[User]) = hasRole(course, prior.who, CourseRole.staff, prior.cache)
  }  

  case class ViewTask(task:Ref[Task]) extends PermOnIdRef[User, Task](task) {
    def resolve(prior:Approval[User]) = for (
        t <- prior.cache(task, classOf[Task]);
        a <- prior ask ViewCourse(t.course)
    ) yield a
  }  
  
  case class EditTask(task:Ref[Task]) extends PermOnIdRef[User, Task](task) {
    def resolve(prior:Approval[User]) = for (
        t <- prior.cache(task, classOf[Task]);
        a <- prior ask EditCourse(t.course)
    ) yield a
  }  

  case class EditOutput(to:Ref[TaskOutput]) extends PermOnIdRef[User, TaskOutput](to) {
    def resolve(prior:Approval[User]) = {
      (for (
        o <- to if (!o.finalised.isDefined && o.byUser.getId == prior.who.getId)
      ) yield Approved("You may write edit your own output")) orIfNone Refused("You may only edit your own critiques, before they have been finalised")
    }
  }  
  
  case class WriteCritique(gca:Ref[GroupCritAllocation]) extends PermOnIdRef[User, GroupCritAllocation](gca) {
    def resolve(prior:Approval[User]) = {
      for (g <- gca if (g.user.getId == prior.who.getId)) yield Approved("You may write critiques that are allocated to you")
    }
  }  
  
  def getRoles(course: Ref[Course], user: Ref[User]) = {
    for (
       u <- user;
       r <- u.registrations.find(_.course.getId == course.getId)
    ) yield r.roles
  }
  
  def hasRole(course:Ref[Course], user:Ref[User], role:CourseRole.T, cache:LookUpCache):Ref[Approved] = {
    (
      for (
        roles <- getRoles(cache(course, classOf[Course]), user) if roles.contains(role)
      ) yield Approved(s"You have role $role for this course")
    ) orIfNone Refused(s"You do not have role $role for this course")
  }  
}