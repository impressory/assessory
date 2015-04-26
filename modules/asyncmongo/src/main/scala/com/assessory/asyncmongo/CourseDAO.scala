package com.assessory.asyncmongo

import com.assessory.api._
import com.wbillingsley.handy._
import com.wbillingsley.handy.mongodbasync.DAO

import converters.BsonHelpers._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

object CourseDAO extends DAO(DB, classOf[Course], "course") {

  /**
   * Saves the user's details
   */
  def saveDetails(c:Course) = updateAndFetch(
    query= "_id" $eq c.id,
    update=$set(
      "title" -> c.title,
      "shortName" -> c.shortName,
      "shortDescription" -> c.shortDescription,
      "website" -> c.website,
      "coverImage" -> c.coverImage
    )
  )

  /**
   * Save a new user. This should only be used for new users because it overwrites
   * sessions and identities.
   */
  def saveNew(c:Course) = saveSafe(c)

}
