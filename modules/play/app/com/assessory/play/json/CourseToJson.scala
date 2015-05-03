package com.assessory.play.json

import com.assessory.api._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.Course
import com.wbillingsley.handy.user.User
import com.wbillingsley.handyplay.JsonConverter
import play.api.libs.json.{JsValue, Json}

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
