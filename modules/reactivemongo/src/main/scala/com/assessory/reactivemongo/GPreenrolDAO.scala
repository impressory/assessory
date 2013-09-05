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

object GPreenrolDAO extends DAO[GPreenrol] {

  val clazz = classOf[GPreenrol]
  
  val collName = "gpreenrol"
    
  val db = DBConnector
  
  def unsaved = GPreenrol(id = allocateId)
  
  implicit val gppFormat = Macros.handler[GPreenrolPair]
  
  implicit val gpeFormat = Macros.handler[GPreenrol]
  
  implicit object bsonReader extends BSONDocumentReader[GPreenrol] {
    def read(doc:BSONDocument):GPreenrol = {
      new GPreenrol(
        id = doc.getAs[BSONObjectID]("_id").get.stringify,
        course = doc.getAs[Ref[Course]]("course").getOrElse(RefNone),
        set = doc.getAs[Ref[GroupSet]]("set").getOrElse(RefNone),
        groupData = doc.getAs[Seq[GPreenrolPair]]("groupData").getOrElse(Seq.empty),
        created = doc.getAs[Long]("created").getOrElse(System.currentTimeMillis())
      )
    }
  }  

  /**
   * Save a new user. This should only be used for new users because it overwrites
   * sessions and identities.
   */
  def saveNew(p:GPreenrol) = saveSafe(
    BSONDocument(
      idIs(p.id),
      "course" -> p.course,
      "set" -> p.set,
      "groupData" -> p.groupData,
      "created" -> p.created
    ),
    p
  )
  
  def byCourse(c:Ref[Course]) = findMany(BSONDocument("course" -> c))
  
  def byIdentity(service:String, value:String, username:Option[String]) = findMany(
    username match {
      case Some(u) => BSONDocument("$or" -> Seq(
          BSONDocument("groupData.service" -> service, "groupData.value" -> value, "groupData.used" -> false),
          BSONDocument("groupData.service" -> service, "groupData.username" -> username, "groupData.used" -> false)
      ))
      case None => BSONDocument("service" -> service, "value" -> value)
    }
  )
  
  def useRow(service:String, value:String, username:Option[String]) = {
    for (p <- byIdentity(service, value, username)) yield {
      val row = p.groupData.indexWhere { row => 
        row.service == service && !row.used && (Some(row.username) == username || row.value == value)
      }      
      updateUnsafe(
          query=BSONDocument(idIs(p.id)), 
          update=BSONDocument("$set" -> BSONDocument(s"identities.$$${row}.used" -> true)), 
          item=p, upsert=false
      )
      p
    }
  }
}