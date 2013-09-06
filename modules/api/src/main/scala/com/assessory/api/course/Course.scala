package com.assessory.api.course

import com.wbillingsley.handy.{Ref, RefNone, HasStringId}
import com.assessory.api._

case class Course (

    id:String,
    
    title: Option[String] = None,
    
    shortName:Option[String] = None,
    
    shortDescription:Option[String] = None,
    
    website:Option[String] = None,
    
    coverImage:Option[String] = None,

    addedBy:Ref[User] = RefNone,
    
    created:Long = System.currentTimeMillis
    
) extends HasStringId
