package com.assessory.api.group

import com.assessory.api.User
import com.assessory.api.course.Course
import com.wbillingsley.handy.{Ref, RefNone, RefManyById, HasStringId}

case class Group (
    
  id:String,
  
  course:Ref[Course] = RefNone,
  
  set:Ref[GroupSet] = RefNone,
  
  name:Option[String] = None,
  
  provenance:Option[String] = None,
  
  members:RefManyById[User, String] = RefManyById.empty(classOf[User]),
  
  created:Long = System.currentTimeMillis

) extends HasStringId