package com.assessory.api.critique

import com.wbillingsley.handy._
import com.assessory.api._
import com.assessory.api.Task
import com.wbillingsley.handy.appbase.{Answer, Question, TaskBody, GroupSet}

case class Critique(
  target: Target[_],

  answers: Seq[Answer[_]]
) extends TaskOutputBody {
  val kind = CritiqueTask.kind
}



abstract class CritTargetStrategy(val kind: String) extends HasKind

case class MyOutputStrategy(
  task: Id[Task,String]
) extends CritTargetStrategy("outputs relevant to me")

case class OfMyGroupsStrategy(
  task: RefWithId[Task]
) extends CritTargetStrategy(OfMyGroupsStrategy.kind)

object OfMyGroupsStrategy {
  val kind = "critiques of my groups"
}

case class PreallocateGroupStrategy(
  set: Id[GroupSet, String],

  number: Int
) extends CritTargetStrategy(PreallocateGroupStrategy.kind)

object PreallocateGroupStrategy {
  val kind = "group"
}


case class CritiqueTask (
  questionnaire: Seq[Question],

  strategy: CritTargetStrategy
) extends TaskBody {

  val kind = CritiqueTask.kind

}

object CritiqueTask {

  val kind = "Critique"

}


case class AllocatedCrit(
  target: Target[_],

  critique: Option[Id[TaskOutput, String]] = None
)


case class CritAllocation(

  id: Id[CritAllocation,String],

  task: Id[Task, String],

  completeBy: Target[_],

  allocation: Seq[AllocatedCrit] = Seq.empty

) extends HasStringId[CritAllocation]
