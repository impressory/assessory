package com.assessory.api.group

import com.wbillingsley.handy.{Ref, RefNone, RefFailed, HasStringId}
import Ref._
import com.assessory.api.course.Course
import com.assessory.api._

object GPreenrol {
  case class GroupData(group:Ref[Group], lookups:Seq[IdentityLookup])
}

case class GPreenrol (

    id: String, 
    
    course: Ref[Course] = None,
    
    set: Ref[GroupSet] = None,
    
    groupData: Seq[GPreenrol.GroupData] = Seq.empty,
    
    created: Long = System.currentTimeMillis

) extends HasStringId




