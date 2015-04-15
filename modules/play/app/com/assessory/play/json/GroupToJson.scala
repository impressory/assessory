package com.assessory.play.json

import com.wbillingsley.handyplay.JsonConverter
import com.assessory.api._
import com.assessory.reactivemongo._
import course._
import group._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.{Writes, Json, JsValue}

object GroupToJson extends JsonConverter[Group, User] {
  
  implicit val gFormat = new Writes[Group] {

    def writes(g:Group) = Json.obj(
      "id" -> g.id,
      "parent" -> g.parent,
      "course" -> g.course,
      "set" -> g.set,
      "name" -> g.name,
      "provenance" -> g.provenance,
      "members" -> g.members,
      "created" -> g.created
    )

  }
  
  def toJsonFor(g:Group, a:Approval[User]):Ref[JsValue] = {

    import Permissions._

    for {
      // We only return the JSON if they can view it...
      mayView <- a ask ViewGroup(g.itself)
      view = true
      edit <- a askBoolean EditGroup(g.itself)
    } yield {
      val p = Json.obj(
        "permissions" -> Json.obj(
          "view" -> view,
          "edit" -> edit
        )
      )
      gFormat.writes(g) ++ p
    }
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