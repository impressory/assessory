package com.assessory.reactivemongo

import com.wbillingsley.handy.reactivemongo._
import reactivemongo.api._
import reactivemongo.bson._
import com.wbillingsley.handy.{Ref, RefWithId, RefNone, RefManyById}
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.UserProvider

import com.assessory.api._
import course._
import group._
import question._

object TaskOutputDAO extends DAO {

  type DataT = TaskOutput
  
  val clazz = classOf[TaskOutput]
  
  val collName = "taskOutput"
    
  val db = DBConnector

  val executionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  implicit val tobHandler = TaskOutputBodyHandler
  
  def unsaved = TaskOutput(id = allocateId)
  
  implicit object bsonReader extends BSONDocumentReader[TaskOutput] {
    def read(doc:BSONDocument):TaskOutput = {
      new TaskOutput(
        id = doc.getAs[BSONObjectID]("_id").get.stringify,
        task = doc.getAs[RefWithId[Task]]("task").getOrElse(RefNone),
        byUser = doc.getAs[RefWithId[User]]("byUser").getOrElse(RefNone),
        byGroup = doc.getAs[RefWithId[Group]]("byGroup").getOrElse(RefNone),
        attnUsers = doc.getAs[RefManyById[User, String]]("attnUsers").getOrElse(RefManyById.empty),
        attnGroups = doc.getAs[RefManyById[Group, String]]("attnGroups").getOrElse(RefManyById.empty),
        body = doc.getAs[TaskOutputBody]("body"),
        created = doc.getAs[Long]("created").getOrElse(System.currentTimeMillis()),
        finalised = doc.getAs[Long]("finalised"),
        updated = doc.getAs[Long]("updated").getOrElse(System.currentTimeMillis())
      )
    }
  }  

  /**
   * Save a new user. This should only be used for new users because it overwrites
   * sessions and identities.
   */
  def saveNew(t:TaskOutput) = {
    val d = BSONDocument(
      idIs(t.id),
      "task" -> t.task,
      "byUser" -> t.byUser, "byGroup" -> t.byGroup, "attnUsers" -> t.attnUsers, "attnGroups" -> t.attnGroups,
      "body" -> t.body, "created" -> t.created, "updated" -> t.updated
    )
    saveSafe(d,t)
  }
  
  def updateBody(t:TaskOutput) = {
    for (
      b <- Ref(t.body);
      updated <- updateAndFetch(
        query=BSONDocument(idIs(t.id)),
        update=BSONDocument("$set" -> TaskOutputBodyHandler.bodyUpdate(b))
      )
    ) yield updated
  }  
  
  def finalise(t:TaskOutput) = updateAndFetch(
    query=BSONDocument(idIs(t.id)),
    update=BSONDocument("$set" -> BSONDocument("finalised" -> System.currentTimeMillis()))
  )
  
  def byTask(t:Ref[Task]) = {
    for {
      tid <- id(t)
      to <- findMany(BSONDocument("task" -> tid))
    } yield to
  }
  
  def byTaskAndUser(t:Ref[Task], u:Ref[User]) = {
    for {
      tid <- id(t)
      uid <- id(u)
      to <- findMany(BSONDocument("task" -> tid, "byUser" -> uid))
    } yield to
  }

  def byPartialBody(t:Ref[Task], b:TaskOutputBody, without:Seq[String]) = {
    for {
      tid <- id(t)
      bson = TaskOutputBodyHandler.write(b)
      filtered = BSONDocument(
        for {
          (el, v) <- bson.elements if (!without.contains(el))
        } yield (s"body.${el}", v)
      )
      to <- {
        val query = BSONDocument("task" -> tid) ++ filtered
        findMany(query)
      }
    } yield to
  }
  
  def relevantTo(t:Task, u:Ref[User]) = {
    val groupIds = GroupDAO.byCourseAndUser(t.course, u).map(_.id)
    for {
      uid <- id(u)
      gids <- groupIds.toRefOne.map(_.toSeq);
      gg = RefManyById(gids).of[Group]
      to <- {
        val d = BSONDocument(
        "task" -> (t.itself:RefWithId[Task]),
        "$or" -> BSONArray(
          BSONDocument("attnUsers" -> uid),
          BSONDocument("attnGroups" -> BSONDocument("$in" -> gg))
        )
        )
        findMany(d)
      }
    } yield to
  }
}