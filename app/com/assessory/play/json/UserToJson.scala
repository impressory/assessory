package com.assessory.play.json

import com.wbillingsley.handy.appbase.JsonConverter
import com.assessory.api.User
import com.wbillingsley.handy.{Approval, RefNone}
import com.wbillingsley.handy.Ref._
import play.api.libs.json.Json
import play.api.libs.json.JsValue

object UserToJson extends JsonConverter[User, User] {
  
  def toJsonFor(u:User, a:Approval[User]) = {    
    if (u.itself.getId == a.who.getId) {      
      Json.obj(
        "id" -> u.id,
        "nickname" -> u.nickname,
        "avatar" -> u.avatar
      ).itself      
    } else {
      // We don't hand out user details to other users at the moment
      Json.obj("id" -> u.id).itself
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