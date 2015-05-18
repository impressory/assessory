package com.assessory.api

import com.wbillingsley.handy.{Ids, Id, HasStringId, HasKind}
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

  open: Due = NoDue,

  due: Due = NoDue,

  closed: Due = NoDue

)

sealed trait Due extends HasKind {
  /**
   * Given a student's group memberships, when is this due?
   */
  def due(groups:Ids[Group,String]):Option[Long]
}

case class DueDate(time:Long) extends Due {
  val kind = DueDate.kind

  def due(groups:Ids[Group,String]) = Some(time)
}
object DueDate {
  val kind = "date"
}

case class DuePerGroup(times:Map[Id[Group, String], Long]) extends Due {
  val kind = DuePerGroup.kind

  def due(groups:Ids[Group,String]) = {
    val i = times.keySet.intersect(groups.toSeqId.toSet).headOption
    i.map(times.apply)
  }
}
object DuePerGroup {
  val kind = "per group"
}

case object NoDue extends Due {
  val kind = "none"

  def due(groups:Ids[Group,String]) = None
}

