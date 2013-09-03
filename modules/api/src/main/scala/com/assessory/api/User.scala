package com.assessory.api

import com.wbillingsley.handy.HasStringId

import com.wbillingsley.handy.appbase.User._

case class User(
    
    id:String,
    
    name:Option[String] = None,
    
    nickname:Option[String] = None,
    
    avatar:Option[String] = None,
    
    pwlogin: PasswordLogin = PasswordLogin(),
    
    identities:Seq[Identity] = Seq.empty,
    
    created: Long = defaultCreated
    
) extends com.wbillingsley.handy.appbase.User[Identity, PasswordLogin] with HasStringId {

}
