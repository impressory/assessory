package com.assessory.reactivemongo

import com.wbillingsley.handy.reactivemongo._
import reactivemongo.api._
import reactivemongo.bson._
import com.wbillingsley.handy.{Ref, RefNone, RefManyById}
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.UserProvider

import com.assessory.api._
import course._
import group._

object GroupDAO extends DAO[Group] {

  val clazz = classOf[Group]
  
  val collName = "assessoryGroup"
    
  val db = DBConnector
  
  def unsaved = Group(id = allocateId)
    
  implicit object bsonReader extends BSONDocumentReader[Group] {
    def read(doc:BSONDocument):Group = {
      new Group(
        id = doc.getAs[BSONObjectID]("_id").get.stringify,
        course = doc.getAs[Ref[Course]]("course").getOrElse(RefNone),
        set = doc.getAs[Ref[GroupSet]]("set").getOrElse(RefNone),
        name= doc.getAs[String]("name"),
        provenance= doc.getAs[String]("provenance"),
        members = new RefManyById(classOf[User], doc.getAs[Seq[String]]("members").getOrElse(Seq.empty)),
        created = doc.getAs[Long]("created").getOrElse(System.currentTimeMillis)
      )
    }
  }  

  /**
   * Save a new user. This should only be used for new users because it overwrites
   * sessions and identities.
   */
  def saveNew(g:Group) = saveSafe(
    BSONDocument(
      idIs(g.id),
      "course" -> g.course,
      "set" -> g.set,
      "name" -> g.name,
      "provenance" -> g.provenance,
      "members" -> g.members.getIds,
      "created" -> g.created
    ),
    g
  )
  
  def byCourse(c:Ref[Course]) = findMany(BSONDocument("course" -> c))
  
  def byCourseAndUser(c:Ref[Course], u:Ref[User]) = findMany(BSONDocument("course" -> c, "members" -> u))
  
  def bySet(gs:Ref[GroupSet]) = findMany(BSONDocument("set" -> gs))
  
  def byNames(names:Set[String]) = findMany(BSONDocument("name" -> BSONDocument("$in" -> names)))
}