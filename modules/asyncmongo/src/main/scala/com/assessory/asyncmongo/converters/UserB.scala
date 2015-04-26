package com.assessory.asyncmongo.converters


import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import com.assessory.api.{ActiveSession, Identity, User}
import com.wbillingsley.handy.user.PasswordLogin
import org.bson.{BsonDocument, BsonValue}

import BsonHelpers._

import scala.util.Try

object UserB extends BsonDocumentConverter[User] {
  override def write(i: User) = bsonDoc(
    "_id" -> i.id,
    "name" -> i.name,
    "nickname" -> i.nickname,
    "avatar" -> i.avatar,
    "secret" -> i.secret,
    "activeSessions" -> i.activeSessions,
    "pwlogin" -> i.pwlogin,
    "identities" -> i.identities,
    "created" -> i.created
  )

  override def read(doc: BsonDocument): Try[User] = Try {
    new User(
      id = doc.getObjectId("_id"),
      name  = doc.getString("name"),
      nickname = doc.getString("nickname"),
      avatar = doc.getString("avatar"),
      secret = doc.getString("secret"),
      activeSessions = doc.getObjSeq[ActiveSession]("activeSessions"),
      pwlogin = doc.getObject[PasswordLogin]("pwlogin"),
      identities = doc.getObjSeq[Identity]("identities"),
      created = doc.getInt64("created")
    )
  }
}
