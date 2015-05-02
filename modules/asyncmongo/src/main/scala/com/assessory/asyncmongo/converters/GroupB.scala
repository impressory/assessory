package com.assessory.asyncmongo.converters

import com.wbillingsley.handy.appbase.{GroupSet, Course, Group}
import com.wbillingsley.handy.{Ids, Id}
import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import org.bson.BsonDocument

import BsonHelpers._

import scala.util.Try

object GroupB extends BsonDocumentConverter[Group] {
  override def write(i: Group) = bsonDoc(
    "_id" -> i.id,
    "course" -> i.course,
    "set" -> i.set,
    "parent" -> i.parent,
    "name" -> i.name,
    "provenance" -> i.provenance,
    "members" -> i.members,
    "created" -> i.created
  )

  override def read(doc: BsonDocument): Try[Group] = Try {
    new Group(
      id = doc.req[Id[Group, String]]("_id"),
      course = doc.opt[Id[Course, String]]("course"),
      set = doc.req[Id[GroupSet, String]]("set"),
      parent = doc.opt[Id[Group, String]]("parent"),
      name = doc.opt[String]("name"),
      provenance = doc.opt[String]("provenance"),
      members = doc.req[Ids[Group.Reg, String]]("members"),
      created = doc.req[Long]("created")
    )
  }
}
