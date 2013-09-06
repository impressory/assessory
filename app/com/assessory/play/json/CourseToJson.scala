package com.assessory.play.json

import com.wbillingsley.handy.appbase.JsonConverter
import com.assessory.api._
import course._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.Json
import play.api.libs.json.JsValue

object CourseToJson extends JsonConverter[Course, User] {
  
  implicit val courseFormat = Json.writes[Course]
  
  def toJsonFor(c:Course, a:Approval[User]) = {
    
    val permissions = for (
      view <- optionally(a ask Permissions.EditCourse(c.itself));
      edit <- optionally(a ask Permissions.EditCourse(c.itself))
    ) yield Json.obj(
      "view" -> view.isDefined,
      "edit" -> edit.isDefined
    )
    
    for (p <- permissions) yield courseFormat.writes(c) ++ Json.obj("permissions" -> p)
  }
  
  def toJson(c:Course) = courseFormat.writes(c).itself
  
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