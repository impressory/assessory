package com.assessory.asyncmongo

import com.assessory.api._
import com.assessory.api.critique._
import com.assessory.asyncmongo.converters.BsonHelpers._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.User
import com.wbillingsley.handy.mongodbasync.DAO

object CritAllocationDAO extends DAO(DB, classOf[CritAllocation], "critAllocation") {

  def byTask(t:Ref[Task]) = {
    for {
      tId <- t.refId
      d <- findMany("task" $eq tId)
    } yield d
  }

  def byUserAndTask(u:Ref[User], t:Ref[Task]) = {
    for {
      uId <- u.refId
      tId <- t.refId
      d <- findMany(("task" $eq tId) and ("user" $eq uId))
    } yield d
  }

  def saveNew(gca:CritAllocation) = saveSafe(gca)

  def setOutput(alloc:Id[CritAllocation,String], target:Target, output:Id[TaskOutput,String]) = updateAndFetch(
    query=("_id" $eq alloc) and ("allocation.target" $eq target),
    update=$set("allocation.$.critique" -> output)
  )

}
