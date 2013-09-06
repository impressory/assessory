package com.assessory.reactivemongo

import com.wbillingsley.handy.reactivemongo._
import reactivemongo.api._
import reactivemongo.bson._
import com.wbillingsley.handy.{Ref, RefNone, RefManyById}
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.UserProvider

import com.assessory.api._
import course._
import group._
import groupcrit._

object GroupCritAllocationDAO extends DAO[GroupCritAllocation] {
  
  val clazz = classOf[GroupCritAllocation]
  
  val collName = "groupCritAllocation"
    
  val db = DBConnector
  
  implicit val preallHandler = Macros.handler[GCPreallocate]
  
  implicit object GCAllocatedCritHandler extends BSONHandler[BSONDocument, GCAllocatedCrit] {
    def read(doc:BSONDocument) = {
      GCAllocatedCrit(
        group = doc.getAs[Ref[Group]]("group").getOrElse(RefNone),
        critique = doc.getAs[Ref[GCritique]]("critique").getOrElse(RefNone)
      )
    }
    
    def write(crit:GCAllocatedCrit) = BSONDocument(
      "group" -> crit.group,
      "critique" -> crit.critique
    )
  }
  
  def unsaved = GroupCritAllocation(id = allocateId)
    
  implicit object bsonReader extends BSONDocumentReader[GroupCritAllocation] {
    def read(doc:BSONDocument):GroupCritAllocation = {
      new GroupCritAllocation(
        id = doc.getAs[BSONObjectID]("_id").get.stringify,
        task = doc.getAs[Ref[Task]]("task").getOrElse(RefNone),
        user = doc.getAs[Ref[User]]("user").getOrElse(RefNone),
        preallocate = doc.getAs[GCPreallocate]("preallocate"),
        allocation = doc.getAs[Seq[GCAllocatedCrit]]("allocation").getOrElse(Seq.empty)
      )
    }
  }  
  

}