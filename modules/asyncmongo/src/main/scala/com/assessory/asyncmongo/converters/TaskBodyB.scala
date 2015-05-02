package com.assessory.asyncmongo.converters


import com.wbillingsley.handy.Id
import com.wbillingsley.handy.appbase.{GroupSet, Question}
import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import com.assessory.api._
import critique._
import org.bson.BsonDocument

import BsonHelpers._

import scala.util.{Failure, Try}

object TaskBodyB extends BsonDocumentConverter[TaskBody] {
  override def write(i: TaskBody) = i match {
    case c:CritiqueTask => CritiqueTaskB.write(c)
  }

  override def read(doc: BsonDocument): Try[TaskBody] = {
    doc.req[String]("kind") match {
      case CritiqueTask.kind => CritiqueTaskB.read(doc)
      case k => Failure(new IllegalStateException("Couldn't parse task body with kind " + k))
    }
  }
}

object CritiqueTaskB extends BsonDocumentConverter[CritiqueTask] {
  implicit val targStratB = CritTargetStrategyB
  implicit val questionB = QuestionB

  override def write(i: CritiqueTask) = bsonDoc(
    "kind" -> i.kind,
    "strategy" -> i.strategy,
    "questionnaire" -> i.questionnaire
  )

  override def read(doc: BsonDocument): Try[CritiqueTask] = Try {
    CritiqueTask(
      strategy = doc.req[CritTargetStrategy]("strategy"),
      questionnaire = doc.req[Seq[Question]]("questionnaire")
    )
  }
}


object CritTargetStrategyB extends BsonDocumentConverter[CritTargetStrategy] {
  override def write(i: CritTargetStrategy) = {
    val doc = bsonDoc("kind" -> i.kind)
    i match {
      case MyOutputStrategy(task) => doc.append("task", task)
      case OfMyGroupsStrategy => doc
      case PreallocateGroupStrategy(set, number) =>
        doc.append("set",set)
          .append("number",number)
    }
  }

  override def read(doc: BsonDocument): Try[CritTargetStrategy] = {
    doc.req[String]("kind") match {
      case MyOutputStrategy.kind => Try { MyOutputStrategy(task = doc.req[Id[Task,String]]("task")) }
      case OfMyGroupsStrategy.kind => Try { OfMyGroupsStrategy }
      case PreallocateGroupStrategy.kind => Try { PreallocateGroupStrategy(
        set = doc.req[Id[GroupSet,String]]("set"),
        number = doc.req[Int]("number")
      ) }
      case k => Failure(new IllegalStateException("Couldn't parse task body with kind " + k))
    }
  }
}
