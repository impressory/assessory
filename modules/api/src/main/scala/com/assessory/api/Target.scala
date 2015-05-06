package com.assessory.api

import com.wbillingsley.handy.{Id, HasKind}
import com.wbillingsley.handy.appbase._

abstract class Target extends HasKind {
  val id: Id[_, String]
}

case class UnrecognisedT(id:Id[Nothing, String], original:String) extends Target { val kind = original }

case class TargetUser(id:Id[User, String]) extends Target { val kind = TargetUser.kind }
object TargetUser {
  val kind = "User"
}

case class TargetCourseReg(id:Id[Course.Reg, String]) extends Target{ val kind = TargetCourseReg.kind }
object TargetCourseReg {
  val kind = "CourseReg"
}

case class TargetGroup(id:Id[Group, String]) extends Target{ val kind = TargetGroup.kind }
object TargetGroup {
  val kind = "Group"
}

case class TargetTaskOutput(id:Id[TaskOutput, String]) extends Target{ val kind = TargetTaskOutput.kind }
object TargetTaskOutput {
  val kind = "TaskOutput"
}


object Target {
  def from(id:String, kind:String) = kind match {
    case "User" => TargetUser(Id(id))
    case _ => UnrecognisedT(Id(id), kind)
  }

}
