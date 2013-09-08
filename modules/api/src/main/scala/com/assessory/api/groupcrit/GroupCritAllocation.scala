package com.assessory.api.groupcrit

import com.wbillingsley.handy._
import com.assessory.api._
import group._

case class GCAllocatedCrit(

  group: Ref[Group] = RefNone,
  
  critique: Ref[TaskOutput] = RefNone
)

case class GroupCritAllocation(
    
  id: String,
  
  task: Ref[Task] = RefNone,
    
  user: Ref[User] = RefNone,
  
  preallocate: Option[IdentityLookup] = None, 
  
  allocation: Seq[GCAllocatedCrit] = Seq.empty
  
) extends HasStringId

