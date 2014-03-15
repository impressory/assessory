package com.assessory.reactivemongo

import _root_.reactivemongo.bson._
import com.assessory.api._
import question._
import groupcrit._
import group._
import com.wbillingsley.handy._

object GroupCritToBSON {
  
  implicit val qhandler = QuestionHandler
  implicit val qqhandler = QuestionnaireHandler

  def newBSON(g:GroupCritTask) = BSONDocument(
        "groupToCrit" -> g.groupToCrit,
        "withinSet" -> g.withinSet,
        "number" -> g.number,
        "preallocate" -> g.preallocate,
        "questionnaire" -> g.questionnaire,
        "allocated" -> g.allocated
      )
      
  def updateBSON(g:GroupCritTask) = BSONDocument(
        "body.groupToCrit" -> g.groupToCrit,
        "body.withinSet" -> g.withinSet,
        "body.number" -> g.number,
        "body.preallocate" -> g.preallocate,
        "body.questionnaire" -> g.questionnaire
      )
      

}


object GCritiqueToBSON {
  
  import GroupCritToBSON._
  
  implicit val ah = AnswerHandler
  
  def newBSON(gc:GCritique) = BSONDocument("forGroup" -> gc.forGroup, "answers" -> gc.answers, "kind" -> GCritique.kind)
  
  def updateBSON(gc:GCritique) = BSONDocument("body.forGroup" -> gc.forGroup, "body.answers" -> gc.answers)
  
  implicit object gcReader extends BSONDocumentReader[GCritique] {
    def read(doc:BSONDocument) = {
      GCritique(
        forGroup = doc.getAs[RefWithId[Group]]("forGroup").getOrElse(RefNone),
        answers = doc.getAs[Seq[Answer]]("answers").getOrElse(Seq.empty)
      )
    }
  }  
  
}