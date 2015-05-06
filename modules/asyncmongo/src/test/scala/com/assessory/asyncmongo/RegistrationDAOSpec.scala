package com.assessory.asyncmongo

import com.wbillingsley.handy.Id._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.{Group, GroupSet, CourseRole, Course, User}
import com.wbillingsley.handy.mongodbasync.FuturifySRC
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeEach


class RegistrationDAOSpec extends Specification with BeforeEach {

  def before = {
    //DB.executionContext = scala.concurrent.ExecutionContext.global
    DB.dbName = "testAssessory_reg"
    scala.concurrent.blocking {
      FuturifySRC.void(DB.db.drop)
    }
  }

  sequential

  "RegistrationDAO" should {

    "Retrieve course registrations it saves" in  {

      val u = User(
        id = Id(UserDAO.allocateId),
        name = Some("Bob")
      )

      val cid = CourseDAO.allocateId.asId[Course]
      val rid = RegistrationDAO.course.allocateId.asId[Course.Reg]

      val c = new Course(
        id = cid,
        title = Some("TestCourse"),
        addedBy = rid
      )

      val r = new Course.Reg(
        id = rid,
        user = u.id,
        target = c.id,
        roles = Set(CourseRole.staff),
        provenance = EmptyKind
      )

      (for {
        savedU <- UserDAO.saveNew(u)
        savedC <- CourseDAO.saveNew(c)
        savedR <- RegistrationDAO.course.saveSafe(r)
        retrieved <- RegistrationDAO.course.byUserAndTarget(savedU.id, savedC.id)
      } yield retrieved.roles).toFuture must beEqualTo(Set(CourseRole.staff)).await
    }

    "Retrieve group registrations it saves" in  {

      val u = User(
        id = Id(UserDAO.allocateId),
        name = Some("Bob")
      )

      val cid = CourseDAO.allocateId.asId[Course]
      val rid = RegistrationDAO.course.allocateId.asId[Course.Reg]

      val c = new Course(
        id = cid,
        title = Some("TestCourse"),
        addedBy = rid
      )

      val r = new Course.Reg(
        id = rid,
        user = u.id,
        target = c.id,
        roles = Set(CourseRole.staff),
        provenance = EmptyKind
      )

      val gs = new GroupSet(
        id = GroupSetDAO.allocateId.asId,
        course = cid,
        name = Some("Test GS")
      )

      val g = new Group(
        id = GroupDAO.allocateId.asId,
        set = gs.id,
        course = Some(cid),
        name = Some("Test G")
      )

      (for {
        savedU <- UserDAO.saveNew(u)
        savedC <- CourseDAO.saveNew(c)
        savedR <- RegistrationDAO.course.saveSafe(r)
        savedGS <- GroupSetDAO.saveNew(gs)
        savedG <- GroupDAO.saveNew(g)
        retrieved <- GroupDAO.addMember(savedG.itself, savedU.itself)
      } yield retrieved.members.ids.size).toFuture must beEqualTo(1).await
    }

  }

}
