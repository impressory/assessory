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
  
  val attnUsers: RefManyById[User, String] = new RefManyById(classOf[User], Seq()),

  val attnGroups: RefManyById[Group, String] = new RefManyById(classOf[Group], Seq()),
  
  val body: Option[TaskOutputBody] = None,
  
  val created:Long = System.currentTimeMillis,
  
  val updated:Long = System.currentTimeMillis
) extends HasStringId 
