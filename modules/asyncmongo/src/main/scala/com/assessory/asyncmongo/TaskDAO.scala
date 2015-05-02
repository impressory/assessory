package com.assessory.asyncmongo

import com.assessory.api._
import com.assessory.asyncmongo.converters.BsonHelpers._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.Course
import com.wbillingsley.handy.mongodbasync.DAO

object TaskDAO extends DAO(DB, classOf[Task], "task") {

  def updateBody(t:Task) = updateAndFetch(
    query="_id" $eq t.id,
    update=$set("body" -> t.body)
  )

  def byCourse(c:Ref[Course]) = {
    for {
      cid <- c.refId
      t <- findMany("course" $eq cid)
    } yield t
  }

}

