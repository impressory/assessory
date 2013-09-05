package com.assessory.play.json

import com.wbillingsley.handy.appbase.JsonConverter
import com.assessory.api._
import course._
import group._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.Json
import play.api.libs.json.JsValue

object GPreenrolToJson extends JsonConverter[GPreenrol, User] {
  
  implicit val gpFormat = Json.writes[GPreenrolPair]
  implicit val gpeFormat = Json.writes[GPreenrol]
  
  def toJsonFor(gp:GPreenrol, a:Approval[User]) = {
    
    val permissions = for (
      course <- a.cache(gp.course, classOf[Course]);
      view <- optionally(a ask Permissions.ViewCourse(course.itself));
      edit <- optionally(a ask Permissions.EditCourse(course.itself))
    ) yield Json.obj(
      "view" -> view.isDefined,
      "edit" -> edit.isDefined
    )
    
    for (p <- permissions) yield gpeFormat.writes(gp) ++ Json.obj("permissions" -> p)
  }
  
  def toJson(gp:GPreenrol) = gpeFormat.writes(gp).itself
  
  /**
   * Produces an update Course object
   */
  def update(gp:GPreenrol, json:JsValue) = {
    gp.copy(
        // TODO
    )
  }
  
  /**
   * Produces an update Course object
   */
  def updateWithCsv(p:GPreenrol, json:JsValue) = {
    
    GPreenrol.fromCsv(
      id = p.id,
      course = p.course,
      set = p.set,
      csv = (json \ "csv").asOpt[String].get,
      created = p.created
    )
  }   

}