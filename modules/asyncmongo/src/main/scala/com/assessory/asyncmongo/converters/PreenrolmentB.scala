package com.assessory.asyncmongo.converters


import com.assessory.asyncmongo.converters.BsonHelpers._
import com.assessory.asyncmongo.converters.RegistrationB._
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.appbase._
import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import org.bson.BsonDocument

import scala.util.Try

class PreenrolmentB[W, T,R,RT](implicit r:ToFromBson[R]) extends BsonDocumentConverter[Preenrolment[W, T,R,RT]] {

  implicit val prb = new PreenrolmentRowB[T,R,RT]

  override def write(i: Preenrolment[W,T,R,RT]) = bsonDoc(
    "_id" -> i.id,
    "name" -> i.name,
    "rows" -> i.rows,
    "modified" -> i.modified,
    "created" -> i.created
  )

  override def read(doc: BsonDocument): Try[Preenrolment[W,T,R,RT]] = Try {
    new Preenrolment[W,T,R,RT](
      id = doc.req[Id[Preenrolment[W,T,R,RT], String]]("_id"),
      name = doc.opt[String]("name"),
      rows = doc.req[Seq[Preenrolment.Row[T,R,RT]]]("rows"),
      modified = doc.req[Long]("modified"),
      created = doc.req[Long]("created")
    )
  }
}

object PreenrolmentB {
  implicit val group:PreenrolmentB[GroupSet, Group, GroupRole, Group.Reg] = new PreenrolmentB
  implicit val course:PreenrolmentB[Course, Course, CourseRole, Course.Reg] = new PreenrolmentB
}

class PreenrolmentRowB[T,R,UT](implicit r:ToFromBson[R]) extends BsonDocumentConverter[Preenrolment.Row[T,R,UT]] {

  implicit val usedB = new UsedB[UT]

  override def write(i: Preenrolment.Row[T,R,UT]) = bsonDoc(
    "target" -> i.target,
    "identity" -> i.identity,
    "roles" -> i.roles.toSeq,
    "used" -> i.used
  )

  override def read(doc: BsonDocument): Try[Preenrolment.Row[T,R,UT]] = Try {
    new Preenrolment.Row[T,R,UT](
      target = doc.req[Id[T,String]]("target"),
      identity = doc.req[IdentityLookup]("identity"),
      roles = doc.req[Seq[R]]("roles").toSet,
      used = doc.opt[Used[UT]]("used")
    )
  }
}

object IdentityLookupB extends BsonDocumentConverter[IdentityLookup] {

  override def write(i: IdentityLookup) = bsonDoc(
    "service" -> i.service,
    "username" -> i.username,
    "value" -> i.value
  )

  override def read(doc: BsonDocument): Try[IdentityLookup] = Try {
    new IdentityLookup(
      service = doc.req[String]("service"),
      username = doc.opt[String]("username"),
      value = doc.opt[String]("value")
    )
  }
}

class UsedB[UT] extends BsonDocumentConverter[Used[UT]] {
  override def write(i: Used[UT]) = bsonDoc(
    "target" -> i.target,
    "time" -> i.time
  )

  override def read(doc: BsonDocument): Try[Used[UT]] = Try {
    new Used[UT](
      target = doc.req[Id[UT,String]]("target"),
      time = doc.req[Long]("time")
    )
  }
}


