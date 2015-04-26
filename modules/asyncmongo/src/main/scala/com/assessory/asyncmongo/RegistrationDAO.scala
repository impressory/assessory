package com.assessory.asyncmongo

import com.assessory.api._
import com.assessory.api.wiring.RegistrationProvider
import com.assessory.asyncmongo.converters.RegistrationB
import com.wbillingsley.handy.appbase.GroupRole
import com.wbillingsley.handy.{EmptyKind, Ref, Id, HasKind}

import com.wbillingsley.handy.mongodbasync.DAO

import converters.BsonHelpers._
import Id._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

class RegistrationDAO[T, R, P <: HasKind](collName:String)(implicit r:RegistrationB[T, R, P])
  extends DAO(DB, classOf[Registration[T,R,P]], collName)
  with RegistrationProvider[T, R, P] {

  override def byUserAndTarget(user: Id[User, String], target: Id[T, String]): Ref[Registration[T, R, P]] = {
    findOne(("user" $eq user) and ("target" $eq target))
  }

  def byTarget(target:Id[T, String]) = findMany("target" $eq target)

  def byUser(user:Id[User, String]) = findMany("user" $eq user)

  def register(user:Id[User, String], target:Id[T, String], roles:Set[R], provenance:P) = {
    updateAndFetch(
      query = ("user" $eq user) and ("target" $eq target),
      update = $addToSet("roles" -> toBsonSeq(roles.toSeq)(r.rToFromBson))
    ) orIfNone saveSafe(
      Registration[T,R,P](
        id = allocateId.asId,
        user = user,
        target = target,
        roles = roles,
        provenance = provenance
      )
    )
  }

}

object RegistrationDAO {

  val course = new RegistrationDAO[Course, CourseRole, HasKind]("courseRegistration")

  val group = new RegistrationDAO[Group, GroupRole, HasKind]("groupRegistration")

}
