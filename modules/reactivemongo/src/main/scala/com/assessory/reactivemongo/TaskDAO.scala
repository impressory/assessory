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
import question._

object TaskDAO extends DAO[Task] {

  val clazz = classOf[Task]
  
  val collName = "task"
    
  val db = DBConnector
  
  implicit val tdHandler = Macros.handler[TaskDetails]
  implicit val tbHandler = TaskBodyHandler
  
  def unsaved = Task(id = allocateId)
  
  implicit object bsonReader extends BSONDocumentReader[Task] {
    def read(doc:BSONDocument):Task = {
      new Task(
        id = doc.getAs[BSONObjectID]("_id").get.stringify,
        course = doc.getAs[Ref[Course]]("course").getOrElse(RefNone),
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
  
  def byCourse(c:Ref[Course]) = findMany(BSONDocument("course" -> c))
  

}