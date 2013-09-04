package com.assessory.reactivemongo

import com.wbillingsley.handy.reactivemongo._
import reactivemongo.api._
import reactivemongo.bson._
import com.wbillingsley.handy.{Ref, RefNone}
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.UserProvider

import com.assessory.api._
import course._

object PreenrolDAO extends DAO[Preenrol] {

  val clazz = classOf[Preenrol]
  
  val collName = "preenrol"
    
  val db = DBConnector
  
  def unsaved = Preenrol(id = allocateId)
  
  implicit val ppFormat = Macros.handler[PreenrolPair]
  
  implicit object bsonReader extends BSONDocumentReader[Preenrol] {
    def read(doc:BSONDocument):Preenrol = {
      new Preenrol(
        id = doc.getAs[BSONObjectID]("_id").get.stringify,
        course = doc.getAs[Ref[Course]]("course").getOrElse(RefNone),
        name = doc.getAs[String]("name"),
        identities = doc.getAs[Seq[PreenrolPair]]("identities").getOrElse(Seq.empty),
        created = doc.getAs[Long]("created").getOrElse(System.currentTimeMillis())
      )
    }
  }  

  /**
   * Save a new user. This should only be used for new users because it overwrites
   * sessions and identities.
   */
  def saveNew(p:Preenrol) = saveSafe(
    BSONDocument(
      idIs(p.id),
      "name" -> p.name,
      "course" -> p.course,
      "identities" -> p.identities,
      "created" -> p.created
    ),
    p
  )
  
  def byCourse(c:Ref[Course]) = findMany(BSONDocument("course" -> c))
  
  
}