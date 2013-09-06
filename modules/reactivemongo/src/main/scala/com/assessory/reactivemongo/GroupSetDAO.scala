package com.assessory.reactivemongo


import com.wbillingsley.handy.reactivemongo._
import reactivemongo.api._
import reactivemongo.bson._
import com.wbillingsley.handy.{Ref, RefNone}
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.UserProvider

import com.assessory.api._
import course._
import group._

object GroupSetDAO extends DAO[GroupSet] {

  val clazz = classOf[GroupSet]
  
  val collName = "groupSet"
    
  val db = DBConnector
  
  def unsaved = GroupSet(id = allocateId)
  
  implicit object bsonReader extends BSONDocumentReader[GroupSet] {
    def read(doc:BSONDocument):GroupSet = {
      new GroupSet(
        id = doc.getAs[BSONObjectID]("_id").get.stringify,
        name = doc.getAs[String]("name"),
        description = doc.getAs[String]("description"),
        course = doc.getAs[Ref[Course]]("course").getOrElse(RefNone),
        preenrol = doc.getAs[Ref[GPreenrol]]("preenrol").getOrElse(RefNone),
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
  
  def setPreenrol(gs:Ref[GroupSet], gp:Ref[GPreenrol]) = updateAndFetch(
    query=BSONDocument("_id" -> gs),
    update=BSONDocument("$set" -> BSONDocument("preenrol" -> gp))
  )
  
  def byCourse(c:Ref[Course]) = findMany(BSONDocument("course" -> c))
  
  
}