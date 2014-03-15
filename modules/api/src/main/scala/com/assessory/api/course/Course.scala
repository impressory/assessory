package com.assessory.api.course

import com.wbillingsley.handy._
import com.assessory.api._

case class Course (

    id:String,
    
    title: Option[String] = None,
    
    shortName:Option[String] = None,
    
    shortDescription:Option[String] = None,
    
    website:Option[String] = None,
    
    coverImage:Option[String] = None,

    addedBy:RefWithId[User] = RefNone,
    
    created:Long = System.currentTimeMillis
    
) extends HasStringId
