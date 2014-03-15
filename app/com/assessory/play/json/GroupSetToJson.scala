package com.assessory.play.json

import com.wbillingsley.handy.appbase.JsonConverter
import com.assessory.api._
import com.assessory.reactivemongo._
import course._
import group._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.Json
import play.api.libs.json.JsValue

object GroupSetToJson extends JsonConverter[GroupSet, User] {
  
  implicit val gsFormat = Json.writes[GroupSet]
  
  def toJsonFor(gs:GroupSet, a:Approval[User]) = {
    
    val permissions = for (
      course <- a.cache(gs.course);
      view <- optionally(a ask Permissions.ViewCourse(course.itself));
      edit <- optionally(a ask Permissions.EditCourse(course.itself))
    ) yield Json.obj(
      "view" -> view.isDefined,
      "edit" -> edit.isDefined
    )
    
    for (p <- permissions) yield gsFormat.writes(gs) ++ Json.obj("permissions" -> p)
  }
  
  def toJson(gs:GroupSet) = gsFormat.writes(gs).itself
  
  /**
   * Produces an update Course object
   */
  def update(gs:GroupSet, json:JsValue) = {
    gs.copy(
        name = (json \ "name").asOpt[String]
    )
  }  

}