package com.assessory.api.groupcrit

import com.wbillingsley.handy._
import com.assessory.api._
import group._

case class GCAllocatedCrit(

  group: RefWithId[Group] = RefNone,
  
  critique: RefWithId[TaskOutput] = RefNone
)

case class GroupCritAllocation(
    
  id: String,
  
  task: RefWithId[Task] = RefNone,
    
  user: RefWithId[User] = RefNone,
  
  preallocate: Option[IdentityLookup] = None, 
  
  allocation: Seq[GCAllocatedCrit] = Seq.empty
  
) extends HasStringId

