package com.assessory.play.json

import com.wbillingsley.handyplay.JsonConverter
import com.assessory.api._
import com.assessory.reactivemongo._
import course._
import group._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.{Json, JsValue, JsObject, Writes, Format}
import critique._
import question._
import com.assessory.reactivemongo.TaskDAO
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsError
import play.Logger

object TaskOutputToJson extends JsonConverter[TaskOutput, User] {
  
  implicit val tbodyFormat = TaskOutputBodyFormat
  implicit val tout = new Writes[TaskOutput] {
    def writes(to:TaskOutput) = Json.obj(
      "id" -> to.id,
      "task" -> to.task,
      "byUser" -> to.byUser,
      "byGroup" -> to.byGroup,
      "attnUsers" -> to.attnUsers,
      "attnGroups" -> to.attnGroups,
      "body" -> to.body,
      "created" -> to.created,
      "finalised" -> to.finalised,
      "updated" -> to.updated
    )
  }
  
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

  import CritiqueJson._

  implicit val qFormat = AnswerFormat

  def writes(b:TaskOutputBody):JsValue = {
    val base = b match {
      case g:Critique => gctFormat.writes(g)
    }
    Json.obj("kind" -> b.kind) ++ base
  }
  
  def reads(j:JsValue) = {
    val tb = (j \ "kind").asOpt[String] match {
      case Some(Critique.kind) => gctFormat.reads(j)
      case x => {
        Logger.error("Kind of task output body not found: " + x)
        JsError("Kind of task output body not found: " + x)
      }
    }
    tb
  }  
  
}