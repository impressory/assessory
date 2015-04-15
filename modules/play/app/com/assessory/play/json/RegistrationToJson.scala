package com.assessory.play.json

import com.wbillingsley.handyplay.JsonConverter
import com.assessory.api._
import com.wbillingsley.handy.{Approval, RefNone}
import com.wbillingsley.handy.Ref._
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

import course._

object RegistrationToJson extends JsonConverter[Registration, User] {

  def toJsonFor(r:Registration, a:Approval[User]) = {
    for {
      whoId <- a.who.refId if (whoId == r.user)
      j <- toJson(r)
    } yield j
  }

  def toJson(r:Registration) = Json.obj(
    "id" -> r.id,
    "user" -> r.user,
    "course" -> r.course,
    "roles" -> r.roles,
    "updated" -> r.updated,
    "created" -> r.created
  ).itself


  def update(u:User, json:JsValue) = {
    u.copy(
      name = (json \ "name").asOpt[String],
      nickname = (json \ "nickname").asOpt[String],
      avatar = (json \ "avatar").asOpt[String]
    )
  }




}