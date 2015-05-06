package com.assessory.asyncmongo


import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy._
import com.wbillingsley.handy.mongodbasync.FuturifySRC
import com.wbillingsley.handy.appbase.{ActiveSession, Identity, User}
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeEach


class UserDAOSpec extends Specification with BeforeEach {

  sequential

  def before = {
    //DB.executionContext = scala.concurrent.ExecutionContext.global
    DB.dbName = "testAssessory_user"
    scala.concurrent.blocking {
      FuturifySRC.void(DB.db.drop)
    }
  }

  "UserDAO" should {

    "Retrieve data it puts" in  {

      val u = User(
        id = Id(UserDAO.allocateId),
        name = Some("Algernon U")
      )

      (for {
        saved <- UserDAO.saveNew(u)
        retrieved <- UserDAO.byId(u.id.id)
      } yield retrieved.name).toFuture must beEqualTo(u.name).await
    }

    "Retrieve multiple users" in  {

      val u = User(
        id = Id(UserDAO.allocateId),
        name = Some("Algernon U")
      )

      val u2 = User(
        id = Id(UserDAO.allocateId),
        name = Some("Bertie U")
      )

      (for {
        saved <- UserDAO.saveNew(u)
        saved2 <- UserDAO.saveNew(u2)
        retrieved <- UserDAO.manyById(Seq(u.id.id, u2.id.id))
      } yield retrieved.name).collect.toFuture must beEqualTo(Seq(u.name, u2.name)).await
    }

    "Push an identity" in  {
      val u = User(
        id = Id(UserDAO.allocateId),
        name = Some("Bob U")
      )

      (for {
        saved <- UserDAO.saveNew(u)
        a <- UserDAO.pushIdentity(saved.itself, Identity(service="foo", value=Some("bar")))
        retrieved <- UserDAO.byId(u.id.id)
      } yield retrieved.identities.head.service).toFuture must beEqualTo("foo").await
    }

    "Find a user by a session key" in {
      val u = User(
        id = Id(UserDAO.allocateId),
        name = Some("Session U")
      )

      (for {
        saved <- UserDAO.saveNew(u)
        a <- UserDAO.pushSession(saved.itself, ActiveSession(key="12345", ip="127.0.0.1"))
        retrieved <- UserDAO.bySessionKey("12345")
      } yield retrieved.name).toFuture must beEqualTo(u.name).await

    }

  }

}
