package com.assessory.api.group

import com.wbillingsley.handy._
import com.assessory.api.course.Course

case class GroupSet (
    
    id:String,
    
    name:Option[String] = None,
    
    description:Option[String] = None,
    
    course: RefWithId[Course] = RefNone,

    parent: RefWithId[GroupSet] = RefNone,
    
    preenrol: RefWithId[GPreenrol] = RefNone,
    
    created: Long = System.currentTimeMillis
    
) extends HasStringId