package com.assessory.api

import com.wbillingsley.handy.HasStringId

import com.wbillingsley.handy.user.User._
import com.assessory.api.course._

case class User(
    
    id:String,
    
    surname:Option[String] = None,

    givenName:Option[String] = None,

    preferredName:Option[String] = None,

    name:Option[String] = None,
    
    nickname:Option[String] = None,
    
    avatar:Option[String] = None,
    
    pwlogin: PasswordLogin = PasswordLogin(),

    secret: String = scala.util.Random.alphanumeric.take(8).mkString,
    
    identities:Seq[Identity] = Seq.empty,
    
    activeSessions:Seq[ActiveSession] = Seq.empty,
    
    registrations:Seq[Registration] = Seq.empty,
    
    created: Long = defaultCreated
    
) extends com.wbillingsley.handy.user.User[Identity, PasswordLogin] with HasStringId {

}
