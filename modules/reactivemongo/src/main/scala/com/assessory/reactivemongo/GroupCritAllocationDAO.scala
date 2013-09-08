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
        preallocate = doc.getAs[IdentityLookup]("preallocate"),
        allocation = doc.getAs[Seq[GCAllocatedCrit]]("allocation").getOrElse(Seq.empty)
      )
    }
  }  
  
  def byTask(t:Ref[Task]) = findMany(BSONDocument("task" -> t))
  
  def byUserAndTask(u:Ref[User], t:Ref[Task]) = findMany(BSONDocument("task" -> t, "user" -> u))
  
  def saveNew(gca:GroupCritAllocation) = {
    saveSafe(
      BSONDocument(
        idIs(gca.id),
        "task" -> gca.task,
        "user" -> gca.user,
        "preallocate" -> gca.preallocate,
        "allocation" -> gca.allocation
      ),
      gca
    )
  }
  
  def markTaskAllocated(t:Ref[Task]) = {
    TaskDAO.updateAndFetch(BSONDocument("_id" -> t), BSONDocument("$set" -> BSONDocument("body.allocated" -> true)))
  }
  
  def byIdentity(task:Ref[Task], service:String, value:Option[String], username:Option[String]) = {
    val s = Seq(
      for (v <- value) yield BSONDocument("preallocate.service" -> service, "preallocate.value" -> value, "preallocate.used" -> false),
      for (u <- username) yield BSONDocument("preallocate.service" -> service, "preallocate.username" -> username, "preallocate.used" -> false)
    )
    val either = for (opt <- s; v <- opt) yield v
    findMany(
        BSONDocument("task" -> task, "$or" -> either)
    )
  }
  
  def useRow(task:Ref[Task], user:Ref[User], service:String, value:Option[String], username:Option[String]) = {
    for (
      p <- byIdentity(task, service, value, username);
      u <- updateAndFetch(
        query=BSONDocument(idIs(p.id)),
        update=BSONDocument("$set" -> BSONDocument("user" -> user, "preallocate.used" -> true))
      ) 
    ) yield u
  }  
    

}