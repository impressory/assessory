package com.assessory.reactivemongo

import _root_.reactivemongo.bson._
import com.assessory.api._
import question._
import critique._
import group._
import com.wbillingsley.handy._

object TaskOutputBodyHandler extends BSONHandler[BSONDocument, TaskOutputBody]{

  implicit val qHandler = QuestionHandler
  implicit val qqHandler = QuestionnaireHandler

  
  def read(doc:BSONDocument):TaskOutputBody = {
    
    val kind = doc.getAs[String]("kind").get
    kind match {
      case Critique.kind => CritiqueToBSON.gcReader.read(doc)
    }
    
  }
  
  def write(b:TaskOutputBody):BSONDocument = {
    
    val base = b match {
      case g:Critique => CritiqueToBSON.newBSON(g)
    }
    BSONDocument("kind" -> b.kind) ++ base
    
  }
  
  def bodyUpdate(b:TaskOutputBody):BSONDocument = {
    
    val base = b match {
      case g:Critique => CritiqueToBSON.updateBSON(g)
    }
    base
  }
}

