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
        roles = doc.getAs[Set[CourseRole.T]]("roles").getOrElse(Set.empty),
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
      "roles" -> p.roles,
      "identities" -> p.identities,
      "created" -> p.created
    ),
    p
  )
  
  def byCourse(c:Ref[Course]) = findMany(BSONDocument("course" -> c))
  
  def byIdentity(service:String, value:String, username:Option[String]) = findMany(
    username match {
      case Some(u) => BSONDocument("$or" -> Seq(
          BSONDocument("identities.service" -> service, "identities.value" -> value, "identities.used" -> false),
          BSONDocument("identities.service" -> service, "identities.username" -> username, "identities.used" -> false)
      ))
      case None => BSONDocument("service" -> service, "value" -> value)
    }
  )
  
  def useRow(service:String, value:String, username:Option[String]) = {
    for (p <- byIdentity(service, value, username)) yield {
      val row = p.identities.indexWhere { row => 
        row.service == service && !row.used && (Some(row.username) == username || row.value == value)
      }      
      updateUnsafe(
          query=BSONDocument(idIs(p.id)), 
          update=BSONDocument("$set" -> BSONDocument(s"identities.${row}.used" -> true)), 
          item=p, upsert=false
      )
      p
    }
  }
}