package com.assessory.asyncmongo

import com.wbillingsley.handy._
import org.specs2.mutable.Specification

import com.assessory.api._


class DAOSpec extends Specification {

  sequential

  "UserDAO" should {

    "Retrieve data it puts" in  {

      DB.dbName = "testAssessory2"

      val u = User(
        id = Id(UserDAO.allocateId),
        name = Some("Bob")
      )

      (for {
        saved <- UserDAO.saveNew(u)
        retrieved <- UserDAO.byId(u.id.id)
      } yield retrieved.name).toFuture must beEqualTo(u.name).await
    }

  }

}
