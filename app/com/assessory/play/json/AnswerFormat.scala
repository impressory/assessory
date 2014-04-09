package com.assessory.play.json

import com.wbillingsley.handy.appbase.JsonConverter
import com.assessory.api._
import course._
import group._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.{Json, JsValue, JsObject, Writes, Format, JsSuccess}
import question._
import com.assessory.reactivemongo.TaskDAO


object AnswerFormat extends Format[Answer] {
  
  object stFormat extends Format[ShortTextAnswer] {
    
    def writes(q:ShortTextAnswer) = Json.obj(
      "kind" -> q.kind, "question" -> q.question, "answer" -> q.answer
    )
    
    def reads(j:JsValue) = JsSuccess(ShortTextAnswer(
      question = (j \ "question").asOpt[String],
      answer = (j \ "answer").asOpt[String]
    ))
    
  }
  
  object tbFormat extends Format[TickBoxAnswer] {
    
    def writes(q:TickBoxAnswer) = Json.obj(
      "kind" -> q.kind, "question" -> q.question, "answer" -> q.answer
    )
    
    def reads(j:JsValue) = JsSuccess(TickBoxAnswer(
      question = (j \ "question").asOpt[String],
      answer = (j \ "answer").asOpt[Boolean].orElse(Some(false))
    ))
    
  }

  
  object iFormat extends Format[IntegerAnswer] {
    
    def writes(q:IntegerAnswer) = Json.obj(
      "kind" -> q.kind, "question" -> q.question, "answer" -> q.answer
    )
    
    def reads(j:JsValue) = JsSuccess(IntegerAnswer(
      question = (j \ "question").asOpt[String],
      answer = (j \ "answer").asOpt[Int]
    ))
    
  }
  
  def writes(q:Answer) = {
    val base = q match {
      case s:ShortTextAnswer => stFormat.writes(s)
      case t:TickBoxAnswer => tbFormat.writes(t)
      case i:IntegerAnswer => iFormat.writes(i)
    }
    base
  }
  
  def reads(j:JsValue) = {
    val kind = (j \ "kind").asOpt[String].get
    val q = kind match {
      case ShortTextQuestion.kind => stFormat.reads(j)
      case TickBoxQuestion.kind => tbFormat.reads(j)
      case IntegerQuestion.kind => iFormat.reads(j)
    }
    q
  }
  
}