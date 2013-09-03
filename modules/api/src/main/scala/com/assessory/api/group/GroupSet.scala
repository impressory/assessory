package com.assessory.api.group

import com.wbillingsley.handy.{Ref, RefNone, HasStringId}
import com.assessory.api.course.Course

case class GroupSet (
    
    id:String,
    
    name:Option[String] = None,
    
    course: Ref[Course] = RefNone
    
) extends HasStringId