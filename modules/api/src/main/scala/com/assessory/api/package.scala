package com.assessory

package object api {

  type User = com.wbillingsley.handy.user.User
  val User = com.wbillingsley.handy.user.User

  type Identity = com.wbillingsley.handy.user.Identity
  val Identity = com.wbillingsley.handy.user.Identity

  type ActiveSession = com.wbillingsley.handy.user.ActiveSession
  val ActiveSession = com.wbillingsley.handy.user.ActiveSession

  type PasswordLogin = com.wbillingsley.handy.user.PasswordLogin
  val PasswordLogin = com.wbillingsley.handy.user.PasswordLogin

  /** The service key for the student number in the user's identities */
  val I_STUDENT_NUMBER = "student number"
}

