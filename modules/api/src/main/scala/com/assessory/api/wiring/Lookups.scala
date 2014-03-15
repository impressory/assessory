package com.assessory.api.wiring

import com.wbillingsley.handy._
import com.assessory.api._
import com.assessory.api.group._

object Lookups {

  val a = com.wbillingsley.handy.LookUp

  implicit var user:LookUp[User, String] = LookUp.fails("User lookup has not been configured")

  implicit var group:LookUp[Group, String] = LookUp.fails("Group lookup has not been configured")

  implicit var task:LookUp[Task, String] = LookUp.fails("Task lookup has not been configured")

}
