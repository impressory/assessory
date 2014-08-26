package com.assessory.api.course

import com.wbillingsley.handy._
import com.assessory.api._

case class Registration(

 id: Id[Registration, String],

 user: Id[User, String],

 course: Id[Course, String],

 roles: Set[CourseRole.T] = Set(CourseRole.student),

 updated:Long = System.currentTimeMillis,

 created:Long = System.currentTimeMillis

) extends HasStringId[Registration]

trait RegistrationProvider {
  def byUserAndCourse(user:Id[User, String], course:Id[Course, String]):Ref[Registration]
}

object NullRegistrationProvider extends RegistrationProvider {
  def byUserAndCourse(user:Id[User, String], course:Id[Course, String]):Ref[Registration] = {
    System.err.println("No registration provider has been wired up")
    RefFailed(new IllegalStateException("No registration provider has been wired up"))
  }
}
