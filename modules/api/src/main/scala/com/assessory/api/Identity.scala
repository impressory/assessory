package com.assessory.api

/**
 * A representation of a social login, such as a GitHub account.
 * 
 * This inherits some functionality from appbase
 */
case class Identity (
    
    service: String,
    
    value: String
    
) extends com.wbillingsley.handy.appbase.Identity
