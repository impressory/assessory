package com.assessory.api

import com.wbillingsley.handy.{Ids, Id, HasKind}

abstract class Target[T](val kind:String) extends HasKind {
  val id: Id[T, String]
}

abstract class Targets[T](val kind:String) extends HasKind {
  val ids: Ids[T, String]
}

case class UnrecognisedT(id:Id[Nothing, String], original:String) extends Target(original)

class NoTargets extends Targets[Nothing]("Empty") {
  val ids = Ids.empty[Nothing, String]
}


case class TargetUser(id:Id[User, String]) extends Target[User]("User")

case class TargetCourseReg(id:Id[Course.Reg, String]) extends Target[Course.Reg]("CourseReg")

case class TargetGroup(id:Id[Group, String]) extends Target[Group]("Group")

case class TargetTaskOutput(id:Id[TaskOutput, String]) extends Target[TaskOutput]("TaskOutput")


object Target {
  def from(id:String, kind:String) = kind match {
    case "User" => TargetUser(Id(id))
    case _ => UnrecognisedT(Id(id), kind)
  }

}
