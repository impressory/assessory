package com.assessory.asyncmongo.converters

import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import com.wbillingsley.handy.user.PasswordLogin
import org.bson.{BsonDocument, BsonValue}

import BsonHelpers._

import scala.util.Try

object PwLoginB extends BsonDocumentConverter[PasswordLogin] {
  override def write(i: PasswordLogin) = bsonDoc(
    "email" -> i.email,
    "pwhash" -> i.pwhash,
    "username" -> i.username
  )

  override def read(doc: BsonDocument): Try[PasswordLogin] = Try {
    new PasswordLogin(
      email = doc.opt[String]("email"),
      username  = doc.opt[String]("username"),
      pwhash = doc.opt[String]("pwhash")
    )
  }
}
