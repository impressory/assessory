package com.assessory.asyncmongo.converters


import com.assessory.asyncmongo.converters.BsonHelpers._
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.appbase.{Identity, PasswordLogin, ActiveSession, User}
import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import org.bson.BsonDocument

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
      id = doc.req[Id[User, String]]("_id"),
      name  = doc.opt[String]("name"),
      nickname = doc.opt[String]("nickname"),
      avatar = doc.opt[String]("avatar"),
      secret = doc.req[String]("secret"),
      activeSessions = doc.getObjSeq[ActiveSession]("activeSessions"),
      pwlogin = doc.getObject[PasswordLogin]("pwlogin"),
      identities = doc.getObjSeq[Identity]("identities"),
      created = doc.req[Long]("created")
    )
  }
}
