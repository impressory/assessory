package com.assessory.reactivemongo

import _root_.reactivemongo.bson._
import com.assessory.api._
import question._
import groupcrit._
import group._
import com.wbillingsley.handy._

object TaskBodyHandler extends BSONHandler[BSONDocument, TaskBody]{

  implicit def refWriter[T <: HasStringId] = new BSONWriter[Ref[T], BSONValue] {
    // TODO: this may fail if id is None (which it shouldn't be)
    def write(r:Ref[T]) = {
      r.getId.map(new BSONObjectID(_)).getOrElse(BSONNull)
    }
  }

  implicit val qHandler = QuestionHandler
  
  implicit object questionnaireReader extends BSONDocumentReader[Questionnaire] {
     def read(doc:BSONDocument) = {
       Questionnaire(questions=doc.getAs[Seq[Question]]("questions").getOrElse(Seq.empty))
     }
  }
  
  
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
  
  
  implicit object QuestionnaireWriter extends BSONWriter[Questionnaire, BSONDocument] {
    def write(q:Questionnaire) = BSONDocument(
      "questions" -> q.questions
    )
    
  }
  
  def read(doc:BSONDocument):TaskBody = {
    
    val kind = doc.getAs[String]("kind").get
    kind match {
      case GroupCritTask.kind => groupCritHandler.read(doc)
    }
    
  }
  
  def write(b:TaskBody):BSONDocument = {
    
    val base = b match {
      case g:GroupCritTask => BSONDocument(
        "groupToCrit" -> g.groupToCrit,
        "withinSet" -> g.withinSet,
        "preallocate" -> g.preallocate,
        "questionnaire" -> g.questionnaire,
        "allocated" -> g.allocated
      )
    }
    BSONDocument("kind" -> b.kind) ++ base
    
  }
}