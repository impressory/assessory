package com.assessory.api

import com.wbillingsley.handy.appbase.PasswordLogin._

case class PasswordLogin (
    
    salt: Option[String] = defaultSalt,
    
    pwhash: Option[String] = None,
    
    username: Option[String] = None,
    
    email:Option[String] = None
    
) extends com.wbillingsley.handy.appbase.PasswordLogin