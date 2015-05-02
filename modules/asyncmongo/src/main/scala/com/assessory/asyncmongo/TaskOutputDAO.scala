package com.assessory.asyncmongo

import com.assessory.api._
import com.assessory.asyncmongo.converters.BsonHelpers._
import com.wbillingsley.handy._
import com.wbillingsley.handy.mongodbasync.DAO
object TaskOutputDAO extends DAO(DB, classOf[TaskOutput], "taskOutput") {

  def updateBody(t:TaskOutput) = updateAndFetch(
    query="_id" $eq t.id,
    update=$set("body" -> t.body)
  )

  def finalise(t:TaskOutput) = updateAndFetch(
    query="_id" $eq t.id,
    update=$set("finalised" -> System.currentTimeMillis())
  )

  def byTask(t:Ref[Task]) = {
    for {
      tid <- t.refId
      to <- findMany("task" $eq tid)
    } yield to
  }

  def byTaskAndBy(t:Id[Task,String], by:Target) = {
    findMany (("task" $eq t) and ("by" $eq by))
  }


  def byTaskAndAttn(t:Ref[Task], attn:Target) = {
    for {
      tid <- t.refId
      to <- findMany(("task" $eq tid) and ("attn" $eq attn))
    } yield to
  }

}
