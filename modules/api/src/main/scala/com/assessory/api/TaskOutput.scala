package com.assessory.api

import com.wbillingsley.handy._
import group._


trait TaskOutputBody {
  val kind:String 
}

case class TaskOutput (
  
  val id:String,

  val task:Ref[Task] = RefNone,
  
  val byUser:Ref[User] = RefNone,
  
  val byGroup:Ref[Group] = RefNone,
  
  val attnUsers: RefManyById[User, String] = RefManyById.empty(classOf[User]),

  val attnGroups: RefManyById[Group, String] = RefManyById.empty(classOf[Group]),
  
  val body: Option[TaskOutputBody] = None,
  
  val created:Long = System.currentTimeMillis,
  
  val finalised:Option[Long] = None,
  
  val updated:Long = System.currentTimeMillis
) extends HasStringId 
