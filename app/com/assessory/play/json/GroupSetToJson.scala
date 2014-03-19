package com.assessory.play.json

import com.wbillingsley.handy.appbase.JsonConverter
import com.assessory.api._
import group._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.Json
import play.api.libs.json.JsValue

object GroupSetToJson extends JsonConverter[GroupSet, User] {
  
  implicit val gsFormat = Json.writes[GroupSet]
  
  def toJsonFor(gs:GroupSet, a:Approval[User]) = {

    import Permissions._

    for {
      // We only return the JSON if we can view...
      view <- a ask ViewGroupSet(gs.itself)
      edit <- a askBoolean EditGroupSet(gs.itself)
    } yield {
      gsFormat.writes(gs) ++ Json.obj("permissions" ->
        Json.obj(
          "view" -> true,
          "edit" -> edit
        )
      )
    }
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