package com.assessory.play.json

import com.wbillingsley.handyplay.JsonConverter
import com.assessory.api._
import course._
import group._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.{Writes, Json, JsValue}

import com.assessory.api.wiring.Lookups._

object GPreenrolToJson extends JsonConverter[GPreenrol, User] {
  
  import GPreenrol._
  
  implicit val gpFormat = Json.writes[IdentityLookup]
  implicit val gdFormat = Json.writes[GroupData]
  implicit val gpeFormat = new Writes[GPreenrol] {
    def writes(gp:GPreenrol) = Json.obj(
      "id" -> gp.id,
      "course" -> gp.course,
      "set" -> gp.set,
      "groupData" -> gp.groupData,
      "created" -> gp.created
    )
  }
  
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