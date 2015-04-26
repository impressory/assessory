package com.assessory.asyncmongo

import com.wbillingsley.handy._
import com.wbillingsley.handy.mongodbasync.FuturifySRC
import org.specs2.mutable.Specification

import com.assessory.api._
import Ref._


class UserDAOSpec extends Specification {

  sequential

  def dropUserColl() = FuturifySRC.void(UserDAO.coll.drop).toRef

  "UserDAO" should {

    "Retrieve data it puts" in  {

      DB.dbName = "testAssessory_user"

      val u = User(
        id = Id(UserDAO.allocateId),
        name = Some("Algernon U")
      )

      (for {
       // dropped <- dropUserColl()
        saved <- UserDAO.saveNew(u)
        retrieved <- UserDAO.byId(u.id.id)
      } yield retrieved.name).toFuture must beEqualTo(u.name).await
    }

    "Push an identity" in  {

      DB.dbName = "testAssessory_user"

      val u = User(
        id = Id(UserDAO.allocateId),
        name = Some("Bob U")
      )

      (for {
        //dropped <- dropUserColl()
        saved <- UserDAO.saveNew(u)
        a <- UserDAO.pushIdentity(saved.itself, Identity(service="foo", value=Some("bar")))
        retrieved <- UserDAO.byId(u.id.id)
      } yield retrieved.identities(0).service).toFuture must beEqualTo("foo").await
    }

  }

}
