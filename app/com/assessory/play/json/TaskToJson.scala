package com.assessory.play.json

import com.wbillingsley.handy.appbase.JsonConverter
import com.assessory.api._
import course._
import group._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.{Json, JsValue, JsObject, Writes, Format}
import groupcrit._
import question._
import com.assessory.reactivemongo.TaskDAO
import play.api.libs.json.JsSuccess

object TaskToJson extends JsonConverter[Task, User] {
  
  implicit val tbodyFormat = TaskBodyFormat
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
  def update(t:Task, json:JsValue) = {
    println(json)
    println(t)
    val details = t.details.copy(
        name = (json \ "details" \ "name").asOpt[String],
        description = (json \ "details" \ "description").asOpt[String]
      )
    println("new")
    println(details)
    val task = t.copy(
      details = details,
      body = (json \ "body").asOpt[TaskBody] orElse t.body
    )
    println(task)
    task
  }

}

object QuestionFormat extends Format[Question] {
  
  val stFormat = Json.format[ShortTextQuestion]
  val tbFormat = Json.format[TickBoxQuestion]
  val iFormat = Json.format[IntegerQuestion]
  
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

object TaskBodyFormat extends Format[TaskBody] {
  
  implicit val qFormat = QuestionFormat
  implicit val qsFormat = Json.format[Questionnaire]
  implicit val gctFormat = Json.format[GroupCritTask]
  
  def writes(b:TaskBody):JsValue = {
    val base = b match {
      case g:GroupCritTask => gctFormat.writes(g) 
    }
    Json.obj("kind" -> b.kind) ++ base
  }
  
  def reads(j:JsValue) = {
    val kind = (j \ "kind").asOpt[String].get
    val tb:TaskBody = kind match {
      case GroupCritTask.kind => {
        new GroupCritTask(
          number = (j \ "number").asOpt[Int].getOrElse(1),
          groupToCrit = (j \ "groupToCrit").asOpt[Ref[GroupSet]].getOrElse(RefNone),
          withinSet = (j \ "withinSet").asOpt[Ref[GroupSet]].getOrElse(RefNone),
          questionnaire = (j \ "questionnaire").asOpt[Questionnaire].getOrElse(new Questionnaire)
        )
      }
    }
    JsSuccess(tb)
  }  
  
}