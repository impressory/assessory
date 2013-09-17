package com.assessory.play.json

import com.wbillingsley.handy.appbase.JsonConverter
import com.assessory.api._
import course._
import group._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.{Json, JsValue, JsObject, Writes, Format}
import groupcrit._
import outputcrit._
import question._
import com.assessory.reactivemongo.TaskDAO
import play.api.libs.json.JsSuccess

object TaskOutputToJson extends JsonConverter[TaskOutput, User] {
  
  implicit val tbodyFormat = TaskOutputBodyFormat
  implicit val tout = Json.writes[TaskOutput]
  
  def toJsonFor(t:TaskOutput, a:Approval[User]) = {
        
    val permissions = for (
      edit <- optionally(a ask Permissions.EditOutput(t.itself))
    ) yield Json.obj(
      "edit" -> edit.isDefined
    )
    
    for (p <- permissions) yield tout.writes(t) ++ Json.obj("permissions" -> p)
  }
  
  def toJson(t:TaskOutput) = tout.writes(t).itself
  
  /**
   * Produces an update Course object
   */
  def update(t:TaskOutput, json:JsValue) = {
    val task = t.copy(
      body = (json \ "body").asOpt[TaskOutputBody] orElse t.body
    )
    task
  }

}


object TaskOutputBodyFormat extends Format[TaskOutputBody] {
  
  implicit val qFormat = AnswerFormat
  implicit val gctFormat = Json.format[GCritique]
  implicit val ocFormat = Json.format[OCritique]
  
  def writes(b:TaskOutputBody):JsValue = {
    val base = b match {
      case g:GCritique => gctFormat.writes(g)
      case o:OCritique => ocFormat.writes(o)
    }
    Json.obj("kind" -> b.kind) ++ base
  }
  
  def reads(j:JsValue) = {
    val kind = (j \ "kind").asOpt[String].get
    val tb = kind match {
      case GCritique.kind => gctFormat.reads(j)
      case OCritique.kind => ocFormat.reads(j)
    }
    tb
  }  
  
}