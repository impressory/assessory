package com.assessory.api

import com.wbillingsley.handy.{Id, HasStringId, HasKind}
import com.wbillingsley.handy.appbase._

trait TaskBody extends HasKind

object EmptyTaskBody extends TaskBody {
   val kind = "Empty"
}

case class Task(

  id: Id[Task,String],

  course: Id[Course, String],

  details: TaskDetails = new TaskDetails(),

  body: TaskBody = EmptyTaskBody

) extends HasStringId[Task] {

  def kind = body.kind

}

case class TaskDetails (

  name:Option[String] = None,

  description:Option[String] = None,

  created: Long = System.currentTimeMillis,

  groupSet: Option[Id[GroupSet, String]] = None,

  individual: Boolean = true,

  published: Due = NoDue,

  due: Due = NoDue
)

trait Due extends HasKind

case class DueDate(time:Long) extends Due {
  val kind = DueDate.kind
}
object DueDate {
  val kind = "date"
}

case class DuePerGroup(times:Map[Id[Group, String], Long]) extends Due {
  val kind = DuePerGroup.kind
}
object DuePerGroup {
  val kind = "per group"
}

case object NoDue extends Due {
  val kind = "none"
}

