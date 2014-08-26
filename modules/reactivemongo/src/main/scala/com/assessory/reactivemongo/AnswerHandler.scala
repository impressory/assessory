package com.assessory.reactivemongo

import com.assessory.api._
import question._
import reactivemongo.bson._
import CommonFormats._

object AnswerHandler extends BSONHandler[BSONDocument, Answer] {
  
  val shortTextHandler = Macros.handler[ShortTextAnswer]
  val tickBoxHandler = Macros.handler[TickBoxAnswer]
  val integerHandler = Macros.handler[IntegerAnswer]
  

  def read(doc:BSONDocument):Answer = {
    val kind = doc.getAs[String]("kind").get
    kind match {
      case ShortTextQuestion.kind => shortTextHandler.read(doc)
      case TickBoxQuestion.kind => tickBoxHandler.read(doc)
      case IntegerQuestion.kind => integerHandler.read(doc)
    }
  }
  
  def write(a:Answer):BSONDocument = {
    val base:BSONDocument = a match {
      case s:ShortTextAnswer => shortTextHandler.write(s)
      case t:TickBoxAnswer => tickBoxHandler.write(t)
      case i:IntegerAnswer => integerHandler.write(i)
    }
    BSONDocument("kind" -> a.kind) ++ base
  }
  
}





