package com.assessory.api.critique

import com.assessory.api.question.{Answer, Questionnaire}
import com.assessory.api.group.{Group, GroupSet}
import com.wbillingsley.handy.{HasStringId, RefNone, RefWithId}
import com.assessory.api._
import com.assessory.api.IdentityLookup
import com.assessory.api.group.GroupSet
import com.assessory.api.User
import com.assessory.api.Task
import com.assessory.api.question.Questionnaire

abstract class CritTarget(val kind: String)

case class CTGroup(g: RefWithId[Group]) extends CritTarget("Group")
case class CTTaskOutput(t: RefWithId[TaskOutput]) extends CritTarget("TaskOutput")

case class Critique(
  target: CritTarget,

  answers: Seq[Answer]
) extends TaskOutputBody {

  val kind = Critique.kind

}

object Critique {
  val kind = CritiqueTask.kind
}


abstract class CritTargetStrategy(val kind: String)

case class MyOutputStrategy(
  task: RefWithId[Task]
) extends CritTargetStrategy("outputs relevant to me")

case class OfMyGroupsStrategy(
  task: RefWithId[Task]
) extends CritTargetStrategy(OfMyGroupsStrategy.kind)

object OfMyGroupsStrategy {
  val kind = "critiques of my groups"
}

case class PreallocateGroupStrategy(
  set: RefWithId[GroupSet],

  number: Int
) extends CritTargetStrategy(PreallocateGroupStrategy.kind)

object PreallocateGroupStrategy {
  val kind = "group"
}


case class CritiqueTask (
  questionnaire: Questionnaire,

  strategy: CritTargetStrategy
) extends TaskBody {

  val kind = CritiqueTask.kind

}

object CritiqueTask {

  val kind = "Critique"

}


case class AllocatedCrit(
  target: CritTarget,

  critique: RefWithId[TaskOutput] = RefNone
)


case class CritAllocation(

  id: String,

  task: RefWithId[Task] = RefNone,

  user: RefWithId[User] = RefNone,

  allocation: Seq[AllocatedCrit] = Seq.empty

) extends HasStringId