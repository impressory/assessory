package com.assessory.api.wiring

import com.assessory.api.course.{Preenrol, Course, NullRegistrationProvider, RegistrationProvider}
import com.assessory.api.critique.CritAllocation
import com.wbillingsley.handy._
import com.assessory.api._
import com.assessory.api.group._

object Lookups {

  val a = com.wbillingsley.handy.LookUp

  implicit var luUser:LookUp[User, String] = LookUp.fails("User lookup has not been configured")

  implicit var luCourse:LookUp[Course, String] = LookUp.fails("Course lookup has not been configured")

  implicit var luPreenrol:LookUp[Preenrol, String] = LookUp.fails("Preenrol lookup has not been configured")

  implicit var luGroup:LookUp[Group, String] = LookUp.fails("Group lookup has not been configured")

  implicit var luGPreenrol:LookUp[GPreenrol, String] = LookUp.fails("GPreenrol lookup has not been configured")

  implicit var luGroupSet:LookUp[GroupSet, String] = LookUp.fails("GroupSet lookup has not been configured")

  implicit var luTask:LookUp[Task, String] = LookUp.fails("Task lookup has not been configured")

  implicit var luTaskOutput:LookUp[TaskOutput, String] = LookUp.fails("TaskOutput lookup has not been configured")

  implicit var luCritAlloc:LookUp[CritAllocation, String] = LookUp.fails("CritAllocation lookup has not been configured")

  var registrationProvider:RegistrationProvider = NullRegistrationProvider

}
