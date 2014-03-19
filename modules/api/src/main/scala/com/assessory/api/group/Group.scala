package com.assessory.api.group

import com.assessory.api.User
import com.assessory.api.course.Course
import com.wbillingsley.handy._

case class Group (
    
  id:String,

  parent:RefWithId[Group] = RefNone,

  course:RefWithId[Course] = RefNone,
  
  set:RefWithId[GroupSet] = RefNone,

  name:Option[String] = None,
  
  provenance:Option[String] = None,
  
  members:RefManyById[User, String] = RefManyById.empty,
  
  created:Long = System.currentTimeMillis

) extends HasStringId