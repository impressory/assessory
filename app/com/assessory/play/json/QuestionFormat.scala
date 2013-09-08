package com.assessory.play.json

import com.wbillingsley.handy.appbase.JsonConverter
import com.assessory.api._
import course._
import group._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.{Json, JsValue, JsObject, Writes, Format, JsSuccess}
import groupcrit._
import question._
import com.assessory.reactivemongo.TaskDAO


object QuestionFormat extends Format[Question] {
  
  object stFormat extends Format[ShortTextQuestion] {
    
    def writes(q:ShortTextQuestion) = Json.obj(
      "id" -> q.id, "prompt" -> q.prompt, "maxLength" -> q.maxLength, 
      "kind" -> ShortTextQuestion.kind, "required" -> q.required, "active" -> q.active
    )
    
    def reads(j:JsValue) = JsSuccess(ShortTextQuestion(
      id = (j \ "id").asOpt[String].getOrElse(TaskDAO.allocateId),
      prompt = (j \ "prompt").asOpt[String].getOrElse(""),
      maxLength = (j \ "maxLength").asOpt[Int],
      required = (j \ "required").asOpt[Boolean].getOrElse(false),
      active = (j \ "active").asOpt[Boolean].getOrElse(true)
    ))
    
  }
  
  object tbFormat extends Format[TickBoxQuestion] {
    
    def writes(q:TickBoxQuestion) = Json.obj(
      "id" -> q.id, "prompt" -> q.prompt, 
      "kind" -> TickBoxQuestion.kind, "required" -> q.required, "active" -> q.active
    )
    
    def reads(j:JsValue) = JsSuccess(TickBoxQuestion(
      id = (j \ "id").asOpt[String].getOrElse(TaskDAO.allocateId),
      prompt = (j \ "prompt").asOpt[String].getOrElse(""),
      required = (j \ "required").asOpt[Boolean].getOrElse(false),
      active = (j \ "active").asOpt[Boolean].getOrElse(true)
    ))
    
  }

  
  object iFormat extends Format[IntegerQuestion] {
    
    def writes(q:IntegerQuestion) = Json.obj(
      "id" -> q.id, "prompt" -> q.prompt, "max" -> q.max, "min" -> q.min, 
      "kind" -> IntegerQuestion.kind, "required" -> q.required, "active" -> q.active
    )
    
    def reads(j:JsValue) = JsSuccess(IntegerQuestion(
      id = (j \ "id").asOpt[String].getOrElse(TaskDAO.allocateId),
      prompt = (j \ "prompt").asOpt[String].getOrElse(""),
      max = (j \ "max").asOpt[Int],
      min = (j \ "min").asOpt[Int],
      required = (j \ "required").asOpt[Boolean].getOrElse(false),
      active = (j \ "active").asOpt[Boolean].getOrElse(true)
    ))
    
  }
  
  def writes(q:Question) = {
    val base = q match {
      case s:ShortTextQuestion => stFormat.writes(s)
      case t:TickBoxQuestion => tbFormat.writes(t)
      case i:IntegerQuestion => iFormat.writes(i)
    }
    Json.obj("kind" -> q.kind) ++ base
  }
  
  def reads(j:JsValue) = {
    val kind = (j \ "kind").asOpt[String].get
    val withId = (j \ "id").asOpt[String] match {
      case Some(id) => j
      case None => Json.obj("id" -> TaskDAO.allocateId) ++ j.asInstanceOf[JsObject]
    }
    val q = kind match {
      case ShortTextQuestion.kind => stFormat.reads(withId)
      case TickBoxQuestion.kind => tbFormat.reads(withId)
      case IntegerQuestion.kind => iFormat.reads(withId)
    }
    q
  }
  
}