package com.assessory.api

import com.wbillingsley.handy.appbase.Identity._

/**
 * A representation of a social login, such as a GitHub account.
 * 
 * This inherits some functionality from appbase
 */
case class Identity (
    
    service: String,
    
    value: String, 
    
    avatar: Option[String] = None,
    
    username: Option[String] = None,
    
    since: Long = defaultSince
    
) extends com.wbillingsley.handy.appbase.Identity
