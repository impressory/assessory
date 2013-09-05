package com.assessory.api.group

import com.wbillingsley.handy.{Ref, RefNone, HasStringId}
import com.assessory.api.course.Course

case class GPreenrol (

    id: String, 
    
    course: Ref[Course] = None,
    
    set: Ref[GroupSet] = None,
    
    groupData: Seq[GPreenrolPair] = Seq.empty

) extends HasStringId

case class GPreenrolPair(groupName:String, service:String, value:String, username:String, used:Boolean = false)


