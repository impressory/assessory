package com.assessory.asyncmongo

import com.assessory.api._
import com.wbillingsley.handy._
import com.wbillingsley.handy.mongodbasync.DAO

import converters.BsonHelpers._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

object GroupSetDAO extends DAO(DB, classOf[GroupSet], "groupSet") {

  /**
   * Saves the user's details
   */
  def saveDetails(g:GroupSet) = updateAndFetch(
    query= "_id" $eq g.id,
    update=$set(
      "name" -> g.name,
      "description" -> g.description
    )
  )

  /**
   * Save a new user. This should only be used for new users because it overwrites
   * sessions and identities.
   */
  def saveNew(c:GroupSet) = saveSafe(c)


  def setPreenrol(gs:Ref[GroupSet], gp:Ref[Group.Preenrol]) = {
    for {
      gsid <- gs.refId
      gpid <- gp.refId
      gs <- updateAndFetch(
        query=("_id" $eq gsid),
        update=$set("preenrol" -> gpid)
      )
    } yield gs
  }

  def byCourse(c:Ref[Course]) = for {
    cid <- c.refId
    gs <- findMany("course" $eq cid)
  } yield gs


}
