package com.assessory.api

import com.wbillingsley.handy._
import group._


trait TaskOutputBody {
  val kind:String 
}

case class TaskOutput (
  
  val id:String,

  val task:RefWithId[Task] = RefNone,
  
  val byUser:RefWithId[User] = RefNone,
  
  val byGroup:RefWithId[Group] = RefNone,
  
  val attnUsers: RefManyById[User, String] = RefManyById.empty,

  val attnGroups: RefManyById[Group, String] = RefManyById.empty,
  
  val body: Option[TaskOutputBody] = None,
  
  val created:Long = System.currentTimeMillis,
  
  val finalised:Option[Long] = None,
  
  val updated:Long = System.currentTimeMillis
) extends HasStringId 
