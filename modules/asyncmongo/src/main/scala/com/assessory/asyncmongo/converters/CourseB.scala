package com.assessory.asyncmongo.converters

import com.wbillingsley.handy.Id
import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import com.assessory.api.{Course, ActiveSession, Identity, User}
import com.wbillingsley.handy.user.PasswordLogin
import org.bson.{BsonDocument, BsonValue}

import BsonHelpers._

import scala.util.Try

object CourseB extends BsonDocumentConverter[Course] {
  override def write(i: Course) = bsonDoc(
    "_id" -> i.id,
    "title" -> i.title,
    "shortName" -> i.shortName,
    "shortDescription" -> i.shortDescription,
    "website" -> i.website,
    "coverImage" -> i.coverImage,
    "addedBy" -> i.addedBy,
    "created" -> i.created
  )

  override def read(doc: BsonDocument): Try[Course] = Try {
    new Course(
      id = doc.req[Id[Course, String]]("_id"),
      title = doc.opt[String]("title"),
      shortName = doc.opt[String]("shortName"),
      shortDescription = doc.opt[String]("shortDescription"),
      website = doc.opt[String]("website"),
      coverImage = doc.opt[String]("coverImage"),
      addedBy = doc.req[Id[Course.Reg, String]]("addedBy"),
      created = doc.req[Long]("created")
    )
  }
}
