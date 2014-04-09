package com.assessory.reactivemongo

import com.wbillingsley.handy.reactivemongo._
import reactivemongo.bson._
import com.wbillingsley.handy.{RefWithId, Ref, RefNone, RefManyById}
import com.wbillingsley.handy.Ref._

import com.assessory.api._
import course._

object TaskDAO extends DAO {

  type DataT = Task
  
  val clazz = classOf[Task]
  
  val collName = "task"
    
  val db = DBConnector

  val executionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  implicit val tdHandler = Macros.handler[TaskDetails]
  implicit val tbHandler = TaskBodyHandler
  
  def unsaved = Task(id = allocateId)
  
  implicit object bsonReader extends BSONDocumentReader[Task] {
    def read(doc:BSONDocument):Task = {
      new Task(
        id = doc.getAs[BSONObjectID]("_id").get.stringify,
        course = doc.getAs[RefWithId[Course]]("course").getOrElse(RefNone),
        details = doc.getAs[TaskDetails]("details").getOrElse(new TaskDetails),
        body = doc.getAs[TaskBody]("body")
      )
    }
  }  

  /**
   * Save a new user. This should only be used for new users because it overwrites
   * sessions and identities.
   */
  def saveNew(t:Task) = saveSafe(
    BSONDocument(
      idIs(t.id),
      "course" -> t.course,
      "details" -> t.details,
      "body" -> t.body
    ),
    t
  )
  
  def updateBody(t:Task) = {
    for (
      b <- Ref(t.body);
      updated <- updateAndFetch(
        query=BSONDocument(idIs(t.id)),
        update=BSONDocument("$set" -> TaskBodyHandler.bodyUpdate(b))
      )
    ) yield updated
  }  
  
  def byCourse(c:Ref[Course]) = {
    for {
      cid <- id(c)
      t <- findMany(BSONDocument("course" -> cid))
    } yield t
  }


}