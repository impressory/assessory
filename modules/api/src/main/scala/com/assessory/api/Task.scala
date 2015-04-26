package com.assessory.api

import com.wbillingsley.handy.{Id, HasStringId, HasKind}

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

   published: Option[Long] = Some(System.currentTimeMillis),

   due: Option[Long] = None
 )

trait Due extends HasKind

case class DueDate(time:Long) extends Due {
   val kind = "date"
}

case class DuePerGroup(dates:Map[Id[Group, String], Long]) extends Due {
   val kind = "per group"
}

case object NoDue extends Due {
   val kind = "none"
}

