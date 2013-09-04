package com.assessory.reactivemongo

import com.wbillingsley.handy.reactivemongo._
import reactivemongo.api._
import reactivemongo.bson._
import com.wbillingsley.handy.{Ref, RefNone}
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.UserProvider

import com.assessory.api._
import course._

object CourseDAO extends DAO[Course] {

  val clazz = classOf[Course]
  
  val collName = "course"
    
  val db = DBConnector
  
  def unsaved = Course(id = allocateId)
  
  implicit object bsonReader extends BSONDocumentReader[Course] {
    def read(doc:BSONDocument):Course = {
      new Course(
        id = doc.getAs[BSONObjectID]("_id").get.stringify,    
        title = doc.getAs[String]("title"),
        shortName = doc.getAs[String]("shortName"),
        shortDescription = doc.getAs[String]("shortDescription"),
        addedBy = doc.getAs[Ref[User]]("addedBy").getOrElse(RefNone),
        created = doc.getAs[Long]("created").getOrElse(System.currentTimeMillis())
      )
    }
  }  
  
  /**
   * Saves the user's details
   */
  def saveDetails(c:Course) = updateAndFetch(
    query=BSONDocument(idIs(c.id)), 
    update=BSONDocument("$set" -> BSONDocument(
      "title" -> c.title,
      "shortName" -> c.shortName,
      "shortDescription" -> c.shortDescription
    ))
  )

  /**
   * Save a new user. This should only be used for new users because it overwrites
   * sessions and identities.
   */
  def saveNew(c:Course) = saveSafe(
    BSONDocument(
      idIs(c.id),
      "title" -> c.title,
      "shortName" -> c.shortName,
      "shortDescription" -> c.shortDescription,
      "addedBy" -> c.addedBy,
      "created" -> c.created
    ),
    c
  )
  
  
}