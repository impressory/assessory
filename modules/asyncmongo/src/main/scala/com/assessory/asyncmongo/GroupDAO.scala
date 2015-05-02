package com.assessory.asyncmongo

import com.assessory.asyncmongo.converters.BsonHelpers._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.{Course, Group, GroupRole, GroupSet}
import com.wbillingsley.handy.mongodbasync.DAO
import com.wbillingsley.handy.user.User

object GroupDAO extends DAO(DB, classOf[Group], "assessoryGroup") {

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
      query = "_id" $eq gid
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

  def bySet(gsId:Id[GroupSet,String]) = findMany("set" $eq gsId)

  def byNames(gsId:Id[GroupSet, String], names:Set[String]) = {
    findMany(bsonDoc("set" -> gsId, "name" -> bsonDoc("$in" -> names.toSeq)))
  }

  def byNames(names:Set[String]) = findMany(bsonDoc("name" -> bsonDoc("$in" -> names.toSeq)))

}
