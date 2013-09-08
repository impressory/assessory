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
  
  implicit def refWriter[T <: HasStringId] = new BSONWriter[Ref[T], BSONValue] {
    // TODO: this may fail if id is None (which it shouldn't be)
    def write(r:Ref[T]) = {
      r.getId.map(new BSONObjectID(_)).getOrElse(BSONNull)
    }
  }   
  
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