package com.assessory.play.json

import com.wbillingsley.handyplay.JsonConverter
import com.assessory.api._
import com.wbillingsley.handy.{Approval, RefNone}
import com.wbillingsley.handy.Ref._
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

import course._

object UserToJson extends JsonConverter[User, User] {
  
  // TODO: A Json format for identity that does not return all the details (as in time this will include an auth token)
  implicit val identityFormat = Json.format[Identity]
  
  implicit val sessionFormat = Json.format[ActiveSession]
  
  implicit object pwLoginWrites extends Writes[PasswordLogin] {
    
    def writes(pw:PasswordLogin) = Json.obj(
      "email" -> pw.email,
      "username" -> pw.username,
      "passwordSet" -> pw.pwhash.isDefined
    )
    
  }
  
  def toJsonFor(u:User, a:Approval[User]) = {
    for {
      whoId <- a.who.refId
    } yield {
      if (u.itself.getId == whoId) {
        Json.obj(
          "id" -> u.id,
          "nickname" -> u.nickname,
          "avatar" -> u.avatar,
          "identities" -> u.identities,
          "activeSessions" -> u.activeSessions,
          "pwlogin" -> u.pwlogin
        )
      } else {
        // We don't hand out user details to other users at the moment
        Json.obj(
          "id" -> u.id,
          "nickname" -> u.nickname,
          "avatar" -> u.avatar
        )
      }
    }
  }
  
  def toJson(u:User) = toJsonFor(u, Approval(RefNone))
  
  
  def update(u:User, json:JsValue) = {
    u.copy(
        name = (json \ "name").asOpt[String],
        nickname = (json \ "nickname").asOpt[String],
        avatar = (json \ "avatar").asOpt[String]
    )
  }
  
  
  

}