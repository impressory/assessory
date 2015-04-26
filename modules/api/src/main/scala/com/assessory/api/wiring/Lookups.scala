package com.assessory.api.wiring

import com.assessory.api.critique.CritAllocation
import com.wbillingsley.handy._
import com.assessory.api._
import com.wbillingsley.handy.appbase.GroupRole

object Lookups {

  val a = com.wbillingsley.handy.LookUp

  implicit var luUser:LookUp[User, String] = LookUp.fails("User lookup has not been configured")

  implicit var luCReg:LookUp[Course.Reg, String] = LookUp.fails("Course Registration lookup has not been configured")

  implicit var luGReg:LookUp[Group.Reg, String] = LookUp.fails("Group Registration lookup has not been configured")

  implicit var luCourse:LookUp[Course, String] = LookUp.fails("Course lookup has not been configured")

  implicit var luPreenrol:LookUp[Course.Preenrol, String] = LookUp.fails("Preenrol lookup has not been configured")

  implicit var luGroup:LookUp[Group, String] = LookUp.fails("Group lookup has not been configured")

  implicit var luGPreenrol:LookUp[Group.Preenrol, String] = LookUp.fails("GPreenrol lookup has not been configured")

  implicit var luGroupSet:LookUp[GroupSet, String] = LookUp.fails("GroupSet lookup has not been configured")

  implicit var luTask:LookUp[Task, String] = LookUp.fails("Task lookup has not been configured")

  implicit var luTaskOutput:LookUp[TaskOutput, String] = LookUp.fails("TaskOutput lookup has not been configured")

  implicit var luCritAlloc:LookUp[CritAllocation, String] = LookUp.fails("CritAllocation lookup has not been configured")

  var courseRegistrationProvider:RegistrationProvider[Course, CourseRole, EmptyKind.type] = new NullRegistrationProvider

  var groupRegistrationProvider:RegistrationProvider[Group, GroupRole, EmptyKind.type] = new NullRegistrationProvider
}

trait RegistrationProvider[T, R, P <: HasKind] {
  def byUserAndTarget(user:Id[User, String], target:Id[T, String]):Ref[Registration[T, R, P]]
}

class NullRegistrationProvider[T, R, P <: HasKind] extends RegistrationProvider[T, R, P] {
  def byUserAndTarget(user:Id[User, String], target:Id[T, String]) = {
    RefFailed(new IllegalStateException("No registration provider has been wired up"))
  }
}

