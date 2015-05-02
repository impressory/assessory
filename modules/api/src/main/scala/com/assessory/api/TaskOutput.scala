package com.assessory.api

import com.wbillingsley.handy._

trait TaskOutputBody extends HasKind

object EmptyTaskOutputBody extends TaskOutputBody {
  val kind = "Empty"
}

case class TaskOutput (

  id:Id[TaskOutput, String],

  task:Id[Task, String],

  by:Target,

  attn:Seq[Target] = Seq.empty,

  body: TaskOutputBody = EmptyTaskOutputBody,

  created:Long = System.currentTimeMillis,

  finalised:Option[Long] = None,

  updated:Long = System.currentTimeMillis
) extends HasStringId[TaskOutput]
