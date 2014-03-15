package com.assessory.reactivemongo

import com.wbillingsley.handy.reactivemongo._
import reactivemongo.api._
import reactivemongo.bson._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.UserProvider

import com.assessory.api._
import course._

object CourseDAO extends DAO {

  type DataT = Course
  
  val clazz = classOf[Course]
  
  val collName = "course"
    
  val db = DBConnector
  
  def unsaved = Course(id = allocateId)

  val executionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  implicit object bsonReader extends BSONDocumentReader[Course] {
    def read(doc:BSONDocument):Course = {
      new Course(
        id = doc.getAs[BSONObjectID]("_id").get.stringify,    
        title = doc.getAs[String]("title"),
        shortName = doc.getAs[String]("shortName"),
        shortDescription = doc.getAs[String]("shortDescription"),
        website = doc.getAs[String]("website"),
        coverImage = doc.getAs[String]("coverImage"),
        addedBy = doc.getAs[RefWithId[User]]("addedBy").getOrElse(RefNone),
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
      "shortDescription" -> c.shortDescription,
      "website" -> c.website,
      "coverImage" -> c.coverImage
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
      "website" -> c.website,
      "coverImage" -> c.coverImage,
      "addedBy" -> c.addedBy,
      "created" -> c.created
    ),
    c
  )
  
  
}