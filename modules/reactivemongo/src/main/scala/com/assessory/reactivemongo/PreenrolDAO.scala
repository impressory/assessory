package com.assessory.reactivemongo

import com.wbillingsley.handy.reactivemongo._
import reactivemongo.api._
import reactivemongo.bson._
import com.wbillingsley.handy.{Ref, RefNone}
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.UserProvider

import com.assessory.api._
import course._

object PreenrolDAO extends DAO {

  type DataT = Preenrol
  
  val clazz = classOf[Preenrol]
  
  val collName = "preenrol"
    
  val db = DBConnector
  
  def unsaved = Preenrol(id = allocateId)
  
  implicit object bsonReader extends BSONDocumentReader[Preenrol] {
    def read(doc:BSONDocument):Preenrol = {
      new Preenrol(
        id = doc.getAs[BSONObjectID]("_id").get.stringify,
        course = doc.getAs[Ref[Course]]("course").getOrElse(RefNone),
        roles = doc.getAs[Set[CourseRole.T]]("roles").getOrElse(Set.empty),
        name = doc.getAs[String]("name"),
        identities = doc.getAs[Seq[IdentityLookup]]("identities").getOrElse(Seq.empty),
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
  
  def byIdentity(service:String, value:Option[String], username:Option[String]) =  {
    val s = Seq(
      for (v <- value) yield BSONDocument("identities.service" -> service, "identities.value" -> value, "identities.used" -> false),
      for (u <- username) yield BSONDocument("identities.service" -> service, "identities.username" -> username, "identities.used" -> false) 
    )
    val either = for (opt <- s; v <- opt) yield v
    findMany(
      BSONDocument("$or" -> {
        either
      })
    )
  }
  
  def useRow(service:String, value:Option[String], username:Option[String]) = {
    for (
      p <- byIdentity(service, value, username);
      updated <- {
        val row = p.identities.indexWhere { row => 
          row.service == service && !row.used && (
              (username.isDefined && (row.username == username)) || 
              (value.isDefined && (row.value == value))
          )
        }      
        updateAndFetch(
          query=BSONDocument(idIs(p.id)), 
          update=BSONDocument("$set" -> BSONDocument(s"identities.${row}.used" -> true))
        )
      }
    ) yield updated
  }
}