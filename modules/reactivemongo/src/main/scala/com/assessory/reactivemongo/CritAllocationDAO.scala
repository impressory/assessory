package com.assessory.reactivemongo

import com.wbillingsley.handy.reactivemongo._
import reactivemongo.api._
import reactivemongo.bson._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.UserProvider

import com.assessory.api._
import course._
import group._
import critique._

object CritAllocationDAO extends DAO {
  
  type DataT = CritAllocation
  
  val clazz = classOf[CritAllocation]
  
  val collName = "critAllocation"

  val executionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  val db = DBConnector

  import CritiqueToBSON._
  
  def unsaved = CritAllocation(id = allocateId)
    
  implicit object bsonReader extends BSONDocumentReader[CritAllocation] {
    def read(doc:BSONDocument):CritAllocation = {
      new CritAllocation(
        id = doc.getAs[BSONObjectID]("_id").get.stringify,
        task = doc.getAs[RefWithId[Task]]("task").getOrElse(RefNone),
        user = doc.getAs[RefWithId[User]]("user").getOrElse(RefNone),
        allocation = doc.getAs[Seq[AllocatedCrit]]("allocation").getOrElse(Seq.empty)
      )
    }
  }  
  
  def byTask(t:Ref[Task]) = {
    for {
      tId <- id(t)
      d <- findMany(BSONDocument("task" -> tId))
    } yield d
  }
  
  def byUserAndTask(u:Ref[User], t:Ref[Task]) = {
    for {
      uId <- id(u)
      tId <- id(t)
      d <- findMany(BSONDocument("task" -> tId, "user" -> uId))
    } yield d
  }

  def saveNew(gca:CritAllocation) = {
    saveSafe(
      BSONDocument(
        idIs(gca.id),
        "task" -> gca.task,
        "user" -> gca.user,
        "allocation" -> gca.allocation
      ),
      gca
    )
  }
  
  def markTaskAllocated(t:RefWithId[Task]) = {
    TaskDAO.updateAndFetch(BSONDocument("_id" -> t), BSONDocument("$set" -> BSONDocument("body.allocated" -> true)))
  }

  def setOutput(alloc:RefWithId[CritAllocation], target:CritTarget, output:RefWithId[TaskOutput]) = updateAndFetch(
    query=BSONDocument("_id" -> alloc, "allocation.target" -> target),
    update=BSONDocument("$set" -> BSONDocument("allocation.$.critique" -> output))
  )
    

}