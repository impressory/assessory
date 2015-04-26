package com.assessory

import com.wbillingsley.handy.{HasKind, Ids, Id}

package object api {

  type User = com.wbillingsley.handy.user.User
  val User = com.wbillingsley.handy.user.User

  type Registration[T, R, P <: HasKind] = com.wbillingsley.handy.appbase.Registration[T, R, P]
  val Registration = com.wbillingsley.handy.appbase.Registration

  type Course = com.wbillingsley.handy.appbase.Course
  val Course = com.wbillingsley.handy.appbase.Course

  type CourseRole = com.wbillingsley.handy.appbase.CourseRole
  val CourseRole = com.wbillingsley.handy.appbase.CourseRole

  type Group = com.wbillingsley.handy.appbase.Group
  val Group = com.wbillingsley.handy.appbase.Group

  type GroupSet = com.wbillingsley.handy.appbase.GroupSet
  val GroupSet = com.wbillingsley.handy.appbase.GroupSet

  type Preenrolment[T, R, UT] = com.wbillingsley.handy.appbase.Preenrolment[T, R, UT]
  val Preenrolment = com.wbillingsley.handy.appbase.Preenrolment

  type Identity = com.wbillingsley.handy.user.Identity
  val Identity = com.wbillingsley.handy.user.Identity

  type ActiveSession = com.wbillingsley.handy.user.ActiveSession
  val ActiveSession = com.wbillingsley.handy.user.ActiveSession

  type PasswordLogin = com.wbillingsley.handy.user.PasswordLogin
  val PasswordLogin = com.wbillingsley.handy.user.PasswordLogin

  /** The service key for the student number in the user's identities */
  val I_STUDENT_NUMBER = "student number"
}

