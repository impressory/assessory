package com.assessory.asyncmongo


import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy._
import com.wbillingsley.handy.mongodbasync.FuturifySRC
import com.wbillingsley.handy.user.{Identity, User}
import org.specs2.mutable.Specification


class UserDAOSpec extends Specification {

  sequential

  def dropUserColl() = FuturifySRC.void(UserDAO.coll.drop).toRef(DB.executionContext)

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

    "Retrieve multiple users" in  {

      DB.dbName = "testAssessory_user"

      val u = User(
        id = Id(UserDAO.allocateId),
        name = Some("Algernon U")
      )

      val u2 = User(
        id = Id(UserDAO.allocateId),
        name = Some("Bertie U")
      )

      (for {
      // dropped <- dropUserColl()
        saved <- UserDAO.saveNew(u)
        saved2 <- UserDAO.saveNew(u2)
        retrieved <- UserDAO.manyById(Seq(u.id.id, u2.id.id))
      } yield retrieved.name).collect.toFuture must beEqualTo(Seq(u.name, u2.name)).await
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
      } yield retrieved.identities.head.service).toFuture must beEqualTo("foo").await
    }

  }

}
