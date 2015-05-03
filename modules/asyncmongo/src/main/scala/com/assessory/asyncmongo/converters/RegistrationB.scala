package com.assessory.asyncmongo.converters

import com.assessory.asyncmongo.converters.BsonHelpers._
import com.wbillingsley.handy.appbase._
import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import com.wbillingsley.handy.{EmptyKind, HasKind, Id}
import org.bson.{BsonDocument, BsonString, BsonValue}

import scala.util.Try

class RegistrationB[T, R, P <: HasKind](implicit val rToFromBson:ToFromBson[R], val pToFromBson:ToFromBson[P]) extends BsonDocumentConverter[Registration[T, R, P]] {
  override def write(i: Registration[T, R, P]) = bsonDoc(
    "_id" -> i.id,
    "user" -> i.user,
    "target" -> i.target,
    "roles" -> i.roles.toSeq,
    "provenance" -> i.provenance,
    "updated" -> System.currentTimeMillis(),
    "created" -> i.created
  )

  override def read(doc: BsonDocument): Try[Registration[T, R, P]] = Try {
    new Registration[T, R, P](
      id = doc.req[Id[Registration[T, R, P], String]]("_id"),
      user = doc.req[Id[User, String]]("user"),
      target = doc.req[Id[T, String]]("target"),
      roles = doc.req[Seq[R]]("roles").toSet,
      provenance = doc.req[P]("provenance"),
      updated = doc.req[Long]("updated"),
      created = doc.req[Long]("created")
    )
  }
}

object RegistrationB {

  implicit object CourseRoleToFromBson extends ToFromBson[CourseRole] {
    override def fromBson(b: BsonValue): CourseRole = CourseRole(b.asString.getValue)
    override def toBson(i: CourseRole): BsonValue = new BsonString(i.r)
  }

  implicit object GroupRoleToFromBson extends ToFromBson[GroupRole] {
    override def fromBson(b: BsonValue): GroupRole = GroupRole(b.asString.getValue)
    override def toBson(i: GroupRole): BsonValue = new BsonString(i.r)
  }

  implicit object EmptyKindToFromBson extends ToFromBson[HasKind] {
    override def fromBson(b: BsonValue) = EmptyKind
    override def toBson(i: HasKind): BsonValue = bsonDoc("kind" -> new BsonString("empty"))
  }

  val courseRegB = new RegistrationB[Course, CourseRole, HasKind]
  val groupRegB = new RegistrationB[Group, GroupRole, HasKind]
}
