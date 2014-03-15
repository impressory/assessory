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

object GroupSetDAO extends DAO {

  type DataT = GroupSet
  
  val clazz = classOf[GroupSet]
  
  val collName = "groupSet"
    
  val db = DBConnector

  val executionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  def unsaved = GroupSet(id = allocateId)
  
  implicit object bsonReader extends BSONDocumentReader[GroupSet] {
    def read(doc:BSONDocument):GroupSet = {
      new GroupSet(
        id = doc.getAs[BSONObjectID]("_id").get.stringify,
        name = doc.getAs[String]("name"),
        description = doc.getAs[String]("description"),
        course = doc.getAs[RefWithId[Course]]("course").getOrElse(RefNone),
        preenrol = doc.getAs[RefWithId[GPreenrol]]("preenrol").getOrElse(RefNone),
        created = doc.getAs[Long]("created").getOrElse(System.currentTimeMillis())
      )
    }
  }  
  
  /**
   * Saves the user's details
   */
  def saveDetails(g:GroupSet) = updateAndFetch(
    query=BSONDocument(idIs(g.id)), 
    update=BSONDocument("$set" -> BSONDocument(
      "name" -> g.name,
      "description" -> g.description
    ))
  )

  /**
   * Save a new user. This should only be used for new users because it overwrites
   * sessions and identities.
   */
  def saveNew(g:GroupSet) = saveSafe(
    BSONDocument(
      idIs(g.id),
      "name" -> g.name,
      "description" -> g.description,
      "course" -> g.course,
      "created" -> g.created
    ),
    g
  )
  
  def setPreenrol(gs:Ref[GroupSet], gp:Ref[GPreenrol]) = {
    for {
      gsid <- id(gs)
      gpid <- id(gp)
      gs <- updateAndFetch(
        query=BSONDocument("_id" -> gsid),
        update=BSONDocument("$set" -> BSONDocument("preenrol" -> gpid))
      )
    } yield gs
  }
  
  def byCourse(c:Ref[Course]) = for {
    cid <- id(c)
    gs <- findMany(BSONDocument("course" -> cid))
  } yield gs
  
  
}