package com.assessory.asyncmongo.converters

import com.wbillingsley.handy.Id
import com.wbillingsley.handy.appbase._
import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import org.bson.BsonDocument

import BsonHelpers._

import scala.util.{Failure, Try}

object QuestionB extends BsonDocumentConverter[Question] {
  override def write(i: Question) = {
    val doc = bsonDoc("kind" -> i.kind)
    i match {
      case ShortTextQuestion(id,prompt,maxLength) =>
        doc.append("_id",id)
          .append("prompt",prompt)
          .append("maxLength", maxLength)
      case BooleanQuestion(id,prompt) =>
        doc.append("_id",id)
          .append("prompt",prompt)
    }
  }

  override def read(doc: BsonDocument): Try[Question] = {
    doc.req[String]("kind") match {
      case ShortTextQuestion.kind => Try { ShortTextQuestion(
        id = doc.req[Id[Question,String]]("_id"),
        prompt = doc.req[String]("prompt"),
        maxLength = doc.opt[Int]("maxLength")
      )}
      case BooleanQuestion.kind => Try { BooleanQuestion(
        id = doc.req[Id[Question,String]]("_id"),
        prompt = doc.req[String]("prompt")
      )}
      case k => Failure(new IllegalStateException("Couldn't parse question with kind " + k))
    }
  }
}

object AnswerB extends BsonDocumentConverter[Answer[_]] {
  override def write(i: Answer[_]) = {
    val doc = bsonDoc("kind" -> i.kind)
    i match {
      case ShortTextAnswer(question, answer) =>
        doc.append("question",question)
          .append("answer",answer)
      case BooleanAnswer(question,answer) =>
        doc.append("question",question)
          .append("answer",answer)
    }
  }

  override def read(doc: BsonDocument): Try[Answer[_]] = {
    doc.req[String]("kind") match {
      case ShortTextQuestion.kind => Try { ShortTextAnswer(
        question = doc.req[Id[Question,String]]("question"),
        answer = doc.opt[String]("answer")
      )}
      case BooleanQuestion.kind => Try { BooleanAnswer(
        question = doc.req[Id[Question,String]]("question"),
        answer = doc.opt[Boolean]("answer")
      )}
      case k => Failure(new IllegalStateException("Couldn't parse answer with kind " + k))
    }
  }
}
