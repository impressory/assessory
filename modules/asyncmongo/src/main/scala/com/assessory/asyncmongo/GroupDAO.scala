package com.assessory.asyncmongo

import com.assessory.api._
import com.assessory.asyncmongo.converters.BsonHelpers._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.GroupRole
import com.wbillingsley.handy.mongodbasync.DAO
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import Id._

object GroupDAO extends DAO(DB, classOf[Group], "groupSet") {

  /**
   * Save a new group. This should only be used for new groups because it overwrites
   * members.
   */
  def saveNew(c:Group) = saveSafe(c)

  def addMember(g:Ref[Group], u:Ref[User]) = {
    for {
      gid <- g.refId
      uid <- u.refId
      reg <- RegistrationDAO.group.register(uid, gid, Set(GroupRole.member), EmptyKind)
      query = ("_id" $eq gid)
      update = $addToSet("members" -> reg.id)
      updated <- updateAndFetch(query, update)
    } yield updated
  }

  def byCourse(c:Ref[Course]) = {
    for {
      cid <- c.refId
      g <- findMany("course" $eq cid)
    } yield g
  }

  def byCourseAndName(c:Ref[Course], name:String) = {
    for {
      cid <- c.refId
      g <- findOne(("course" $eq cid) and ("name" $eq name))
    } yield g
  }

  def byCourseAndUser(c:Ref[Course], u:Ref[User]) = {
    for {
      cid <- c.refId
      uid <- u.refId
      g <- findMany(("course" $eq cid) and ("members" $eq uid))
    } yield g
  }

  def bySet(gs:Ref[GroupSet]) = {
    for {
      gid <- gs.refId
      g <- findMany("set" $eq gid)
    } yield g
  }

  def byNames(names:Set[String]) = findMany(bsonDoc("name" -> bsonDoc("$in" -> names.toSeq)))

}
