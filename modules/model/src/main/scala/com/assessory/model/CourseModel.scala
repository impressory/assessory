package com.assessory.model

import java.io.StringReader

import au.com.bytecode.opencsv.CSVReader
import com.assessory.api._
import com.assessory.api.client.WithPerms
import com.assessory.api.wiring.Lookups._
import com.assessory.asyncmongo._
import com.wbillingsley.handy.Id._
import com.wbillingsley.handy.Ids._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase._

import scala.collection.JavaConverters._

object CourseModel {

  def withPerms(a:Approval[User], c:Course) = {
    for {
      edit <- a.askBoolean(Permissions.EditCourse(c.itself))
      view <- a.askBoolean(Permissions.ViewCourse(c.itself))
    } yield {
      WithPerms(
        Map(
          "edit" -> edit,
          "view" -> view
        ),
      c)
    }
  }

  /**
   * Creates a course
   */
  def create(a:Approval[User], clientCourse:Course):Ref[WithPerms[Course]] = {
    for {
      u <- a.who orIfNone Refused("You must be logged in to create courses")
      approved <- a ask Permissions.CreateCourse

      // The client cannot set IDs, so we need to generate an ID for the course and the user's registration to it
      cid = CourseDAO.allocateId.asId[Course]
      rid = RegistrationDAO.course.allocateId.asId[Course.Reg]

      // Create the course and registration
      unsavedCourse = clientCourse.copy(id=cid, addedBy=rid)
      reg = new Course.Reg(id=rid, user=u.id, target=cid, provenance=EmptyKind, roles=Set(CourseRole.staff))

      savedCourse <- CourseDAO.saveNew(unsavedCourse)
      savedReg <- RegistrationDAO.course.saveSafe(reg)

      wp <- withPerms(a, savedCourse)
    } yield wp
  }

  /**
   * Retrieves a course
   */
  def findMany(oIds:Option[Set[String]]) = {
    for {
      ids <- Ref(oIds) orIfNone UserError("ids were missing")
      course <- RefManyById(ids.toSeq).of[Course]
    } yield course
  }

  def coursePreenrols(a:Approval[User], rCourse:Ref[Course]) = {
    for {
      course <- rCourse
      approved <- a ask Permissions.EditCourse(course.itself)
      preenrol <- PreenrolmentDAO.course.within(course.id)
    } yield preenrol
  }

  /**
   * Creates a course preenrolment
   * @param a user making the request
   * @param name name of the preenrolment
   * @param courseId ID of the course
   * @param roles roles that users should be given
   * @param csv csv of identities, in format { service, value, username }
   * @return
   */
  def createPreenrol(a:Approval[User], name:String, courseId:Id[Course, String], roles:Set[CourseRole], csv:String) = {
    val reader = new CSVReader(new StringReader(csv.trim()))
    val lines = reader.readAll().asScala.toSeq.drop(1)
    reader.close()

    def opt(s:String) = Option(s).filter(_.trim.nonEmpty)

    for {
      approved <- a ask Permissions.EditCourse(courseId.lazily)

      unsaved = new Course.Preenrol(
        id = PreenrolmentDAO.course.allocateId.asId,
        name = Some(name),
        rows = for {
          l <- lines
          il = IdentityLookup(l(0), opt(l(1)), opt(l(2)))
        } yield Preenrolment.Row[Course,CourseRole, Course.Reg](roles, courseId, il)
      )

      saved <- PreenrolmentDAO.course.saveSafe(unsaved)
    } yield saved
  }

  /**
   * Searches for course pre-enrolments, and performs them
   */
  def doPreenrolments(user:User) = {
    for {
      i <- user.identities.toRefMany
      p <- PreenrolmentDAO.course.byIdentity(service=i.service, value=i.value, username=i.username)
      (row, idx) <- p.rows.zipWithIndex.filter{ case (row, idx) => i.matches(row.identity.service, row.identity.value, row.identity.username) }.toRefMany
      reg <- RegistrationDAO.course.register(user.id, row.target, row.roles, EmptyKind)
      used <- PreenrolmentDAO.course.useRow(p.id, idx, reg.id)
    } yield reg
  }

  /**
   * Fetches the courses this user is registered with.
   * Note that this also performs the pre-enrolments
   */
  def myCourses(rUser:Ref[User]) = {
    for {
      u <- rUser
      courseIds <- RegistrationDAO.course.byUser(u.id).map(_.target).toIds
      c <- courseIds.lookUp
    } yield c
  }

  def usersInCourse(a:Approval[User], courseId:Id[Course, String]):RefMany[User] = {
    def rUserIds = (for {
      c <- RegistrationDAO.course.byTarget(courseId)
    } yield c.user).collect

    for {
      approved <- a ask Permissions.EditCourse(courseId.lazily)
      userIds <- rUserIds
      user <- userIds.map(_.id).asIds[User].lookUp
    } yield user
  }

}
