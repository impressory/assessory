package com.assessory.asyncmongo.converters

import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import com.assessory.api.{ActiveSession}
import org.bson.{BsonDocument, BsonValue}

import BsonHelpers._

import scala.util.Try

object ActiveSessionB extends BsonDocumentConverter[ActiveSession] {
  override def write(i: ActiveSession) = bsonDoc(
    "ip" -> i.ip,
    "key" -> i.key,
    "since" -> i.since
  )

  override def read(doc: BsonDocument): Try[ActiveSession] = Try {
    new ActiveSession(
      ip = doc.req[String]("ip"),
      key  = doc.req[String]("key"),
      since = doc.req[Long]("since")
    )
  }
}
