package com.assessory.reactivemongo

import com.wbillingsley.handy.reactivemongo._
import reactivemongo.api._
import reactivemongo.bson._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Id._
import Ref._
import com.wbillingsley.handyplay.UserProvider

import CommonFormats._
import com.assessory.api.wiring.Lookups._


import com.assessory.api._
import course._
import group._

object GPreenrolDAO extends DAO {

  type DataT = GPreenrol
  
  val clazz = classOf[GPreenrol]
  
  val collName = "gpreenrol"
    
  val db = DBConnector
  
  def unsaved = GPreenrol(id = allocateId.asId[GPreenrol])

  val executionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  import GPreenrol._
  
  implicit val groupDataReader = Macros.reader[GroupData]
  
  implicit object groupDataWriter extends BSONDocumentWriter[GroupData] {
    def write(gs:GroupData) = {
      BSONDocument("group" -> gs.group, "lookups" -> gs.lookups)
    }
  }
    
  implicit object bsonReader extends BSONDocumentReader[GPreenrol] {
    def read(doc:BSONDocument):GPreenrol = {
      new GPreenrol(
        id = doc.getAs[Id[GPreenrol, String]]("_id").get,
        course = doc.getAs[RefWithId[Course]]("course").getOrElse(RefNone),
        set = doc.getAs[RefWithId[GroupSet]]("set").getOrElse(RefNone),
        groupData = doc.getAs[Seq[GroupData]]("groupData").getOrElse(Seq.empty),
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
  
  def byCourse(c:RefWithId[Course]) = findMany(BSONDocument("course" -> c))
  
  def byIdentity(course:RefWithId[Course], service:String, value:Option[String], username:Option[String]) = {
    val s = Seq(
      for (v <- value) yield BSONDocument("groupData.lookups.service" -> service, "groupData.lookups.value" -> value, "groupData.lookups.used" -> false),
      for (u <- username) yield BSONDocument("groupData.lookups.service" -> service, "groupData.lookups.username" -> username, "groupData.lookups.used" -> false)
    )
    val either = for (opt <- s; v <- opt) yield v
    findMany(
        BSONDocument("course" -> course, "$or" -> either)
    )
  }
  
  def useRow(course:RefWithId[Course], service:String, value:Option[String], username:Option[String]):RefMany[Group] = {
    val rr = for (p <- byIdentity(course, service, value, username)) yield {
      
      for (
        (gd, i) <- p.groupData.zipWithIndex.toRefMany;
        (row, j) <- gd.lookups.zipWithIndex.toRefMany if (
          row.service == service && !row.used && (
              (username.isDefined && (row.username == username)) || 
              (value.isDefined && (row.value == value))
          )            
        );
        updated <- updateAndFetch(
          query=BSONDocument(idIs(p.id)), 
          update=BSONDocument("$set" -> BSONDocument(s"groupData.${i}.lookups.${j}.used" -> true)) 
        ) 
      ) yield gd.group
    }
    rr.flatten.flatten
  }
}