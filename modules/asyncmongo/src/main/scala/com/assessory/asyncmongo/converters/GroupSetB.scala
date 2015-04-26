package com.assessory.asyncmongo.converters


import com.wbillingsley.handy.Id
import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import com.assessory.api._
import com.wbillingsley.handy.user.PasswordLogin
import org.bson.{BsonDocument, BsonValue}

import BsonHelpers._
import PreenrolmentB._

import scala.util.Try

object GroupSetB extends BsonDocumentConverter[GroupSet] {
  override def write(i: GroupSet) = bsonDoc(
    "_id" -> i.id,
    "course" -> i.course,
    "name" -> i.name,
    "description" -> i.description,
    "parent" -> i.parent,
    "preenrol" -> i.preenrol,
    "created" -> i.created
  )

  override def read(doc: BsonDocument): Try[GroupSet] = Try {
    new GroupSet(
      id = doc.req[Id[GroupSet, String]]("_id"),
      course = doc.req[Id[Course, String]]("_id"),
      name = doc.opt[String]("name"),
      description = doc.opt[String]("description"),
      parent = doc.opt[Id[GroupSet, String]]("parent"),
      preenrol = doc.opt[Group.Preenrol]("preenrol"),
      created = doc.req[Long]("created")
    )
  }
}
