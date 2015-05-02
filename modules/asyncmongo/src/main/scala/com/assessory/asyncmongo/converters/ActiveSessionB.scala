package com.assessory.asyncmongo.converters

import com.assessory.asyncmongo.converters.BsonHelpers._
import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import com.wbillingsley.handy.user.ActiveSession
import org.bson.BsonDocument

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
