package com.assessory.asyncmongo.converters


import com.assessory.api.critique._
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.appbase.{Group, Course, Answer}
import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import com.assessory.api._
import com.wbillingsley.handy.user.User
import org.bson.BsonDocument

import BsonHelpers._

import scala.util.{Failure, Try}

object TaskOutputB extends BsonDocumentConverter[TaskOutput] {
  implicit val targetB = TargetB
  implicit val taskOutputBodyB = TaskOutputBodyB

  override def write(i: TaskOutput) = bsonDoc(
    "_id" -> i.id,
    "task" -> i.task,
    "by" -> i.by,
    "attn" -> i.attn,
    "body" -> i.body,
    "created" -> i.created,
    "finalised" -> i.finalised
  )

  override def read(doc: BsonDocument): Try[TaskOutput] = Try {
    new TaskOutput(
      id = doc.req[Id[TaskOutput, String]]("_id"),
      task = doc.req[Id[Task, String]]("task"),
      by = doc.req[Target]("by"),
      attn = doc.req[Seq[Target]]("attn"),
      body = doc.req[TaskOutputBody]("body"),
      created = doc.req[Long]("created"),
      finalised = doc.opt[Long]("finalised")
    )
  }
}

object TargetB extends BsonDocumentConverter[Target] {

  override def write(i: Target) = {
    bsonDoc("kind" -> i.kind, "id" -> i.id)
  }

  override def read(doc: BsonDocument): Try[Target] = {
    doc.req[String]("kind") match {
      case "User" => Try { TargetUser(doc.req[Id[User,String]]("id")) }
      case "CourseReg" => Try { TargetCourseReg(doc.req[Id[Course.Reg,String]]("id")) }
      case "Group" => Try { TargetGroup(doc.req[Id[Group,String]]("id")) }
      case "TaskOutput" => Try { TargetTaskOutput(doc.req[Id[TaskOutput,String]]("id")) }
      case k => Failure(new IllegalStateException("Couldn't parse target with kind " + k))
    }
  }
}

object TaskOutputBodyB extends BsonDocumentConverter[TaskOutputBody] {
  implicit val targetB = TargetB
  implicit val answerB = AnswerB

  override def write(i: TaskOutputBody) = {
    val doc = bsonDoc("kind" -> i.kind)
    i match {
      case c:Critique =>
        doc.append("target",c.target)
          .append("answers", c.answers)
    }
  }

  override def read(doc: BsonDocument): Try[TaskOutputBody] = {
    doc.req[String]("kind") match {
      case CritiqueTask.kind => Try { Critique(
        target = doc.req[Target]("target"),
        answers = doc.req[Seq[Answer[_]]]("answers")
      )}
      case k => Failure(new IllegalStateException("Couldn't parse target with kind " + k))
    }
  }
}
