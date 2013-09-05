package com.assessory.api.group

import com.wbillingsley.handy.{Ref, RefNone, RefFailed, HasStringId}
import Ref._
import com.assessory.api.course.Course

case class GPreenrol (

    id: String, 
    
    course: Ref[Course] = None,
    
    set: Ref[GroupSet] = None,
    
    groupData: Seq[GPreenrolPair] = Seq.empty,
    
    created: Long = System.currentTimeMillis

) extends HasStringId

case class GPreenrolPair(group:Ref[Group], service:String, value:String, username:String, used:Boolean = false)

