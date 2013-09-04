package com.assessory.play.json

import com.wbillingsley.handy.appbase.JsonConverter
import com.assessory.api._
import course._
import group._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.Json
import play.api.libs.json.JsValue

object PreenrolToJson extends JsonConverter[Preenrol, User] {
  
  implicit val ppFormat = Json.writes[PreenrolPair]
  implicit val peFormat = Json.writes[Preenrol]
  
  def toJsonFor(preenrol:Preenrol, a:Approval[User]) = {
    
    val permissions = for (
      course <- a.cache(preenrol.course, classOf[Course]);
      view <- optionally(a ask Permissions.ViewCourse(course.itself));
      edit <- optionally(a ask Permissions.EditCourse(course.itself))
    ) yield Json.obj(
      "view" -> view.isDefined,
      "edit" -> edit.isDefined
    )
    
    for (p <- permissions) yield peFormat.writes(preenrol) ++ Json.obj("permissions" -> p)
  }
  
  def toJson(p:Preenrol) = peFormat.writes(p).itself
  
  /**
   * Produces an update Course object
   */
  def update(p:Preenrol, json:JsValue) = {
    p.copy(
        name = (json \ "name").asOpt[String]
    )
  }
  
  /**
   * Produces an update Course object
   */
  def updateWithCsv(p:Preenrol, json:JsValue) = {
    
    Preenrol.fromCsv(
      id = p.id, 
      name = (json \ "name").asOpt[String], 
      roles = (json \ "roles").asOpt[Seq[String]].getOrElse(Seq.empty).toSet,
      course = p.course, 
      csv = (json \ "csv").asOpt[String].get
    )
  }   

}