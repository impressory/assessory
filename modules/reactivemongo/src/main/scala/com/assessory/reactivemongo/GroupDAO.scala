package com.assessory.reactivemongo

import com.wbillingsley.handy.reactivemongo._
import reactivemongo.api._
import reactivemongo.bson._
import com.wbillingsley.handy._
import Ref._
import Id._
import com.wbillingsley.handyplay.UserProvider

import CommonFormats._
import com.assessory.api.wiring.Lookups._


import com.assessory.api._
import course._
import group._

object GroupDAO extends DAO {

  type DataT = Group
  
  val clazz = classOf[Group]
  
  val collName = "assessoryGroup"
    
  val db = DBConnector

  val executionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  def unsaved = Group(id = allocateId.asId[Group])
    
  implicit object bsonReader extends BSONDocumentReader[Group] {
    def read(doc:BSONDocument):Group = {
      new Group(
        id = doc.getAs[Id[Group,String]]("_id").get,
        course = doc.getAs[RefWithId[Course]]("course").getOrElse(RefNone),
        set = doc.getAs[RefWithId[GroupSet]]("set").getOrElse(RefNone),
        parent = r(doc, "parent")(LookUp),
        name= doc.getAs[String]("name"),
        provenance= doc.getAs[String]("provenance"),
        members = doc.getAs[RefManyById[User, String]]("members").getOrElse(RefManyById.empty),
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
      "parent" -> g.parent,
      "name" -> g.name,
      "provenance" -> g.provenance,
      "members" -> g.members.getIds,
      "created" -> g.created
    ),
    g
  )
  
  def addMember(g:Ref[Group], u:Ref[User]) = {
    for {
      gid <- id(g)
      uid <- id(u)
      query = BSONDocument("_id" -> gid)
      update = BSONDocument("$addToSet" -> BSONDocument("members" -> uid))
      updated <- updateAndFetch(query, update)
    } yield updated
  }
  
  def byCourse(c:Ref[Course]) = {
    for {
      cid <- id(c)
      g <- findMany(BSONDocument("course" -> cid))
    } yield g
  }

  def byCourseAndName(c:Ref[Course], name:String) = {
    for {
      cid <- id(c)
      g <- findOne(BSONDocument("course" -> cid, "name" -> name))
    } yield g
  }
  
  def byCourseAndUser(c:Ref[Course], u:Ref[User]) = {
    for {
      cid <- id(c)
      uid <- id(u)
      g <- findMany(BSONDocument("course" -> cid, "members" -> uid))
    } yield g
  }
  
  def bySet(gs:Ref[GroupSet]) = {
    for {
      gid <- id(gs)
      g <- findMany(BSONDocument("set" -> gid))
    } yield g
  }
  
  def byNames(names:Set[String]) = findMany(BSONDocument("name" -> BSONDocument("$in" -> names)))
}