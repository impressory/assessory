package com.assessory.reactivemongo

import _root_.reactivemongo.bson._
import com.assessory.api._
import question._
import critique._
import group._
import com.wbillingsley.handy._

object TaskBodyHandler extends BSONHandler[BSONDocument, TaskBody]{

  implicit val qHandler = QuestionHandler
  implicit val qqHandler = QuestionnaireHandler

  def read(doc:BSONDocument):TaskBody = {
    
    val kind = doc.getAs[String]("kind").get
    kind match {
      case CritiqueTask.kind => CritiqueTaskToBSON.read(doc)
    }
    
  }
  
  def write(b:TaskBody):BSONDocument = {
    
    val base = b match {
      case g:CritiqueTask => CritiqueTaskToBSON.newBSON(g)
    }
    BSONDocument("kind" -> b.kind) ++ base
    
  }
  
  def bodyUpdate(b:TaskBody):BSONDocument = {
    
    val base = b match {
      case g:CritiqueTask => CritiqueTaskToBSON.updateBSON(g)
    }
    base
  }
}

