package com.assessory.play.json

import com.wbillingsley.handy.appbase.JsonConverter
import com.assessory.api._
import course._
import group._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

object TaskToJson extends JsonConverter[Task, User] {
  
  implicit val tWrites = WritesTaskBody
  implicit val tdFormat = Json.writes[TaskDetails]
  implicit val tFormat = Json.writes[Task]
  
  def toJsonFor(t:Task, a:Approval[User]) = {
    
    val permissions = for (
      course <- a.cache(t.course, classOf[Course]);
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
  def update(gp:GPreenrol, json:JsValue) = {
    gp.copy(
        // TODO
    )
  }

}

import groupcrit._
import question._

object WritesQuestion extends Writes[Question] {
  
  val stWrites = Json.writes[ShortTextQuestion]
  val tbWrites = Json.writes[TickBoxQuestion]
  val iWrites = Json.writes[IntegerQuestion]
  
  def writes(q:Question) = {
    val base = q match {
      case s:ShortTextQuestion => stWrites.writes(s)
      case t:TickBoxQuestion => tbWrites.writes(t)
      case i:IntegerQuestion => iWrites.writes(i)
    }
    Json.obj("kind" -> q.kind) ++ base
  }
  
}

object WritesTaskBody extends Writes[TaskBody] {
  
  implicit val qWrites = WritesQuestion
  implicit val qsFormat = Json.writes[Questionnaire]
  implicit val gctFormat = Json.writes[GroupCritTask]
  
  def writes(b:TaskBody):JsValue = {
    val base = b match {
      case g:GroupCritTask => gctFormat.writes(g) 
    }
    Json.obj("kind" -> b.kind) ++ base
  }
  
}