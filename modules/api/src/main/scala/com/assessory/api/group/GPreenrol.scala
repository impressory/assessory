package com.assessory.api.group

import com.wbillingsley.handy._
import Ref._
import com.assessory.api.course.Course
import com.assessory.api._

object GPreenrol {
  case class GroupData(group:RefWithId[Group], lookups:Seq[IdentityLookup])
}

case class GPreenrol (

    id: Id[GPreenrol,String],
    
    course: RefWithId[Course] = None,
    
    set: RefWithId[GroupSet] = None,
    
    groupData: Seq[GPreenrol.GroupData] = Seq.empty,
    
    created: Long = System.currentTimeMillis

) extends HasStringId[GPreenrol]




