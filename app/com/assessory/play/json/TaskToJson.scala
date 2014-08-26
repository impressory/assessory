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
import play.api.libs.json.JsSuccess
import com.assessory.api.critique.{CritTargetStrategy, CritiqueTask}

import com.assessory.api.wiring.Lookups._

object TaskToJson extends JsonConverter[Task, User] {
  
  implicit val tbodyFormat = TaskBodyFormat
  implicit val tdFormat = Json.writes[TaskDetails]
  implicit val tFormat = new Writes[Task] {
    def writes(t:Task) = Json.obj(
      "id" -> t.id,
      "course" -> t.course,
      "details" -> t.details,
      "body" -> t.body
    )
  }
  
  def toJsonFor(t:Task, a:Approval[User]) = {    
    val permissions = for (
      course <- a.cache(t.course);
      view <- optionally(a ask Permissions.ViewCourse(course.itself));
      edit <- optionally(a ask Permissions.EditCourse(course.itself))
    ) yield Json.obj(
      "view" -> view.isDefined,
      "edit" -> edit.isDefined
    )
    
    for (p <- permissions) yield tFormat.writes(t) ++ Json.obj("permissions" -> p)
  }
  
  def toJson(t:Task) = tFormat.writes(t).itself
  
  /**
   * Produces an update Course object
   */
  def update(t:Task, json:JsValue) = {
    val details = t.details.copy(
        name = (json \ "details" \ "name").asOpt[String],
        description = (json \ "details" \ "description").asOpt[String]
      )
    val task = t.copy(
      details = details,
      body = (json \ "body").asOpt[TaskBody] orElse t.body
    )
    task
  }

}



object TaskBodyFormat extends Format[TaskBody] {

  import CritiqueJson._

  implicit val qFormat = QuestionFormat
  implicit val qsFormat = Json.format[Questionnaire]

  def writes(b:TaskBody):JsValue = {
    val base = b match {
      case g:CritiqueTask => ctFormat.writes(g)
    }
    Json.obj("kind" -> b.kind) ++ base
  }
  
  def reads(j:JsValue) = {
    val kind = (j \ "kind").asOpt[String].get
    val tb:TaskBody = kind match {
      case CritiqueTask.kind => {
        new CritiqueTask(
          questionnaire = (j \ "questionnaire").asOpt[Questionnaire].getOrElse(new Questionnaire),
          strategy = (j \ "strategy").asOpt[CritTargetStrategy].get
        )
      }
    }
    JsSuccess(tb)
  }  
  
}