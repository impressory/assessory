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

object GPreenrolToJson extends JsonConverter[GPreenrol, User] {
  
  import GPreenrol._
  
  implicit val gpFormat = Json.writes[IdentityLookup]
  implicit val gdFormat = Json.writes[GroupData]
  implicit val gpeFormat = Json.writes[GPreenrol]
  
  def toJsonFor(gp:GPreenrol, a:Approval[User]) = {
    
    val permissions = for (
      course <- a.cache(gp.course);
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

}