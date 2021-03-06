package com.assessory.asyncmongo.converters

import com.wbillingsley.handy.appbase.Identity
import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import org.bson.BsonDocument

import BsonHelpers._

import scala.util.Try

object IdentityB extends BsonDocumentConverter[Identity] {
  override def write(i: Identity) = bsonDoc(
    "service" -> i.service,
    "username" -> i.username,
    "value" -> i.value,
    "avatar" -> i.avatar,
    "since" -> i.since
  )

  override def read(doc: BsonDocument): Try[Identity] = Try {
    new Identity(
      service = doc.req[String]("service"),
      value = doc.opt[String]("value"),
      username = doc.opt[String]("username"),
      avatar = doc.opt[String]("avatar"),
      since = doc.req[Long]("since")
    )
  }
}
