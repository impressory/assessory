package com.assessory.play.json

import com.wbillingsley.handy.appbase.JsonConverter
import com.assessory.api._
import course._
import group._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.Json
import play.api.libs.json.JsValue

object GroupToJson extends JsonConverter[Group, User] {
  
  implicit val gFormat = Json.writes[Group]
  
  def toJsonFor(g:Group, a:Approval[User]) = {
    
    val permissions = for (
      course <- a.cache(g.course, classOf[Course]);
      view <- optionally(a ask Permissions.ViewCourse(course.itself));
      edit <- optionally(a ask Permissions.EditCourse(course.itself))
    ) yield Json.obj(
      "view" -> view.isDefined,
      "edit" -> edit.isDefined
    )
    
    for (p <- permissions) yield gFormat.writes(g) ++ Json.obj("permissions" -> p)
  }
  
  def toJson(gp:Group) = gFormat.writes(gp).itself
  
  /**
   * Produces an update Course object
   */
  def update(gp:GPreenrol, json:JsValue) = {
    gp.copy(
        // TODO
    )
  }

}