package com.assessory.reactivemongo

import _root_.reactivemongo.bson._
import com.assessory.api._
import question._
import groupcrit._
import group._
import com.wbillingsley.handy._

object TaskBodyHandler extends BSONHandler[BSONDocument, TaskBody]{

  implicit val qHandler = QuestionHandler
  implicit val qqHandler = QuestionnaireHandler
  
  implicit object groupCritHandler extends BSONDocumentReader[GroupCritTask] {
    def read(doc:BSONDocument) = {
      GroupCritTask(
        number=doc.getAs[Int]("number").getOrElse(1),
        groupToCrit=doc.getAs[Ref[GroupSet]]("groupToCrit").getOrElse(RefNone),
        withinSet=doc.getAs[Ref[GroupSet]]("withinSet").getOrElse(RefNone),
        preallocate=doc.getAs[Boolean]("preallocate").getOrElse(true),
        questionnaire=doc.getAs[Questionnaire]("questionnaire").getOrElse(new Questionnaire),
        allocated=doc.getAs[Boolean]("allocated").getOrElse(false)
      )
    }
  }
  
  def read(doc:BSONDocument):TaskBody = {
    
    val kind = doc.getAs[String]("kind").get
    kind match {
      case GroupCritTask.kind => groupCritHandler.read(doc)
    }
    
  }
  
  def write(b:TaskBody):BSONDocument = {
    
    val base = b match {
      case g:GroupCritTask => GroupCritToBSON.newBSON(g)
    }
    BSONDocument("kind" -> b.kind) ++ base
    
  }
  
  def bodyUpdate(b:TaskBody):BSONDocument = {
    
    val base = b match {
      case g:GroupCritTask => GroupCritToBSON.updateBSON(g)
    }
    base
  }
}

