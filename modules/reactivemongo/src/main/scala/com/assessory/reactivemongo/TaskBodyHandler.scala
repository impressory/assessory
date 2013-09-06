package com.assessory.reactivemongo

import _root_.reactivemongo.bson._
import com.assessory.api._
import question._
import groupcrit._
import com.wbillingsley.handy._

object TaskBodyHandler extends BSONHandler[BSONDocument, TaskBody]{

  implicit def refWriter[T <: HasStringId] = new BSONWriter[Ref[T], BSONValue] {
    // TODO: this may fail if id is None (which it shouldn't be)
    def write(r:Ref[T]) = {
      r.getId.map(new BSONObjectID(_)).getOrElse(BSONNull)
    }
  }

  implicit val qHandler = QuestionHandler
  implicit val questionnaireReader = Macros.reader[Questionnaire]
  implicit val groupCritHandler = Macros.reader[GroupCritTask]
  
  
  implicit object QuestionnaireWriter extends BSONWriter[Questionnaire, BSONDocument] {
    def write(q:Questionnaire) = BSONDocument(
      "name" -> q.name,
      "questions" -> q.questions,
      "created" -> q.created
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
        "questionnair" -> g.questionnaire
      )
    }
    BSONDocument("kind" -> b.kind) ++ base
    
  }
}