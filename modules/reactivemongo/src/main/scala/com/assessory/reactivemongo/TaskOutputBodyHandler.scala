package com.assessory.reactivemongo

import _root_.reactivemongo.bson._
import com.assessory.api._
import question._
import groupcrit._
import outputcrit._
import group._
import com.wbillingsley.handy._

object TaskOutputBodyHandler extends BSONHandler[BSONDocument, TaskOutputBody]{

  implicit val qHandler = QuestionHandler
  implicit val qqHandler = QuestionnaireHandler
  
  implicit object groupCritHandler extends BSONDocumentReader[GroupCritTask] {
    def read(doc:BSONDocument) = {
      GroupCritTask(
        number=doc.getAs[Int]("number").getOrElse(1),
        groupToCrit=doc.getAs[RefWithId[GroupSet]]("groupToCrit").getOrElse(RefNone),
        withinSet=doc.getAs[RefWithId[GroupSet]]("withinSet").getOrElse(RefNone),
        preallocate=doc.getAs[Boolean]("preallocate").getOrElse(true),
        questionnaire=doc.getAs[Questionnaire]("questionnaire").getOrElse(new Questionnaire),
        allocated=doc.getAs[Boolean]("allocated").getOrElse(false)
      )
    }
  }
  
  def read(doc:BSONDocument):TaskOutputBody = {
    
    val kind = doc.getAs[String]("kind").get
    kind match {
      case GCritique.kind => GCritiqueToBSON.gcReader.read(doc)
      case OCritique.kind => OCritiqueToBSON.gcReader.read(doc)
    }
    
  }
  
  def write(b:TaskOutputBody):BSONDocument = {
    
    val base = b match {
      case g:GCritique => GCritiqueToBSON.newBSON(g)
      case g:OCritique => OCritiqueToBSON.newBSON(g)
    }
    BSONDocument("kind" -> b.kind) ++ base
    
  }
  
  def bodyUpdate(b:TaskOutputBody):BSONDocument = {
    
    val base = b match {
      case g:GCritique => GCritiqueToBSON.updateBSON(g)
      case g:OCritique => OCritiqueToBSON.updateBSON(g)
    }
    base
  }
}

