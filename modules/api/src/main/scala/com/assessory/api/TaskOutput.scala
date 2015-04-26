package com.assessory.api

import com.wbillingsley.handy._

trait TaskOutputBody extends HasKind

object EmptyTaskOutputBody extends TaskOutputBody {
  val kind = "Empty"
}

case class TaskOutput (

  val id:Id[TaskOutput, String],

  val task:Id[Task, String],

  val by:Target[_],

  val attn:Targets[_] = new NoTargets,

  val body: TaskOutputBody = EmptyTaskOutputBody,

  val created:Long = System.currentTimeMillis,

  val finalised:Option[Long] = None,

  val updated:Long = System.currentTimeMillis
) extends HasStringId[TaskOutput]
