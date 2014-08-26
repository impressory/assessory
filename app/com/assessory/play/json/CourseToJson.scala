package com.assessory.play.json

import com.wbillingsley.handyplay.JsonConverter
import com.assessory.api._
import course._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.Json
import play.api.libs.json.JsValue

object CourseToJson extends JsonConverter[Course, User] {

  private def core(c:Course) = Json.obj(
    "id" -> c.id,
    "title" -> c.title,
    "shortName" -> c.shortName,
    "shortDescription" -> c.shortDescription,
    "website" -> c.website,
    "coverImage" -> c.coverImage,
    "addedBy" -> c.addedBy,
    "created" -> c.created
  )

  def toJsonFor(c:Course, a:Approval[User]) = {

    val permissions = for (
      view <- optionally(a ask Permissions.EditCourse(c.itself));
      edit <- optionally(a ask Permissions.EditCourse(c.itself))
    ) yield Json.obj(
      "view" -> view.isDefined,
      "edit" -> edit.isDefined
    )
    
    for (p <- permissions) yield core(c) ++ Json.obj("permissions" -> p)
  }
  
  def toJson(c:Course) = core(c).itself
  
  /**
   * Produces an update Course object
   */
  def update(c:Course, json:JsValue) = {
    c.copy(
        title = (json \ "title").asOpt[String],
        shortName = (json \ "shortName").asOpt[String],
        shortDescription = (json \ "shortDescription").asOpt[String],
        coverImage =  (json \ "coverImage").asOpt[String],
        website =  (json \ "website").asOpt[String]
    )
  }  

}