package com.assessory.asyncmongo.converters


import com.assessory.asyncmongo.converters.BsonHelpers._
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.appbase.{Course, GroupSet}
import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import org.bson.BsonDocument

import scala.util.Try

object GroupSetB extends BsonDocumentConverter[GroupSet] {
  override def write(i: GroupSet) = bsonDoc(
    "_id" -> i.id,
    "course" -> i.course,
    "name" -> i.name,
    "description" -> i.description,
    "parent" -> i.parent,
    "created" -> i.created
  )

  override def read(doc: BsonDocument): Try[GroupSet] = Try {
    new GroupSet(
      id = doc.req[Id[GroupSet, String]]("_id"),
      course = doc.req[Id[Course, String]]("course"),
      name = doc.opt[String]("name"),
      description = doc.opt[String]("description"),
      parent = doc.opt[Id[GroupSet, String]]("parent"),
      created = doc.req[Long]("created")
    )
  }
}
