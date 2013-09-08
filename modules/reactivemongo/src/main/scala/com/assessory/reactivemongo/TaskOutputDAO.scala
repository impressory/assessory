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

object TaskOutputDAO extends DAO[TaskOutput] {

  val clazz = classOf[TaskOutput]
  
  val collName = "taskOutput"
    
  val db = DBConnector
  
  implicit val tobHandler = TaskOutputBodyHandler
  
  def unsaved = TaskOutput(id = allocateId)
  
  implicit object bsonReader extends BSONDocumentReader[TaskOutput] {
    def read(doc:BSONDocument):TaskOutput = {
      new TaskOutput(
        id = doc.getAs[BSONObjectID]("_id").get.stringify,
        task = doc.getAs[Ref[Task]]("task").getOrElse(RefNone),
        byUser = doc.getAs[Ref[User]]("byUser").getOrElse(RefNone),
        byGroup = doc.getAs[Ref[Group]]("byGroup").getOrElse(RefNone),
        attnUsers = doc.getAs[RefManyById[User, String]]("attnUsers").getOrElse(new RefManyById(classOf[User], Seq.empty)),
        attnGroups = doc.getAs[RefManyById[Group, String]]("attnGroups").getOrElse(new RefManyById(classOf[Group], Seq.empty)),        
        body = doc.getAs[TaskOutputBody]("body")
      )
    }
  }  

  /**
   * Save a new user. This should only be used for new users because it overwrites
   * sessions and identities.
   */
  def saveNew(t:TaskOutput) = saveSafe(
    BSONDocument(
      idIs(t.id),
      "task" -> t.task,
      "byUser" -> t.byUser, "byGroup" -> t.byGroup, "attnUsers" -> t.attnUsers, "attnGroups" -> t.attnGroups,
      "body" -> t.body, "created" -> t.created, "updated" -> t.updated
    ),
    t
  )
  
  def updateBody(t:TaskOutput) = {
    for (
      b <- Ref(t.body);
      updated <- updateAndFetch(
        query=BSONDocument(idIs(t.id)),
        update=BSONDocument("$set" -> TaskOutputBodyHandler.bodyUpdate(b))
      )
    ) yield updated
  }  
  
  
  

}