package com.assessory.api.question

import com.assessory.api._
import question._
import reactivemongo.bson._

object QuestionHandler extends BSONHandler[BSONDocument, Question] {
  
  val shortTextHandler = Macros.handler[ShortTextQuestion]
  val tickBoxHandler = Macros.handler[TickBoxQuestion]
  val integerHandler = Macros.handler[IntegerQuestion]
  
  def read(doc:BSONDocument):Question = {
    
    val kind = doc.getAs[String]("kind").get
    
    kind match {
      case ShortTextQuestion.kind => shortTextHandler.read(doc)
      case TickBoxQuestion.kind => tickBoxHandler.read(doc)
      case IntegerQuestion.kind => integerHandler.read(doc)
    }
  }
  
  def write(q:Question):BSONDocument = {
    val base:BSONDocument = q match {
      case s:ShortTextQuestion => shortTextHandler.write(s)
      case t:TickBoxQuestion => tickBoxHandler.write(t)
      case i:IntegerQuestion => integerHandler.write(i)
    }
    BSONDocument("kind" -> q.kind) ++ base
  }
  
}

