package com.assessory.model;

import com.assessory.reactivemongo._
import com.assessory.play.json._

import com.assessory.api._
import course._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import play.api.libs.json.JsValue

object CourseModel {


  /**
   * Creates a course
   */
  def create(a:Approval[User], json:JsValue):Ref[JsValue] = {
    for {
      u <- a.who orIfNone Refused("You must be logged in to create courses")
      approved <- a ask Permissions.CreateCourse;
      unsaved = CourseToJson.update(CourseDAO.unsaved.copy(addedBy=u.itself), json);
      saved <- CourseDAO.saveNew(unsaved);
      regPushed <- UserDAO.pushRegistration(u.itself, Registration(course=saved.itself, roles=Seq(CourseRole.student, CourseRole.staff)));

      // When the request began, the user was not registered with this course. We need to produce json for the updated version that is
      j <- CourseToJson.toJsonFor(saved, Approval(regPushed.itself))
    } yield j
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
      preenrol <- PreenrolDAO.byCourse(course.itself)
    } yield preenrol
  }

  def createPreenrol(a:Approval[User], rCourse:Ref[Course], json:JsValue) = {
    for {
      course <- rCourse
      approved <- a ask Permissions.EditCourse(course.itself);
      unsaved <- PreenrolToJson.updateWithCsv(PreenrolDAO.unsaved.copy(course=course.itself), json)
      saved <- PreenrolDAO.saveNew(unsaved)
    } yield saved
  }

  /**
   * Searches for course pre-enrolments, and performs them
   */
  def doPreenrolments(user:User)= {
    val updates = for (
      i <- user.identities.toRefMany;
      p <- PreenrolDAO.useRow(service=i.service, value=Some(i.value), username=i.username);
      pushed <- UserDAO.pushRegistration(user.itself, Registration(course=p.course, roles=p.roles.toSeq))
    ) yield pushed

    // We want to return the user when they have been registered for everything: ie, the last item in the RefMany
    updates.fold(user)((last, updated) => updated)
  }

  /**
   * Fetches the courses this user is registered with.
   * Note that this also performs the pre-enrolments
   */
  def myCourses(rUser:Ref[User]):RefMany[JsValue] = {
    val userAfterUpdates = for (u <- rUser; updated <- doPreenrolments(u)) yield updated

    // As we've updated the user, we'll need a new Approval
    val approval = Approval(userAfterUpdates)

    val rIds = for (
      u <- userAfterUpdates;
      r <- u.registrations.toRefMany;
      cid <- r.course.refId
    ) yield cid


    for (
      ids <- rIds.toRefOne;
      c <- RefManyById(ids.toSeq).of[Course];
      approved <- approval ask Permissions.ViewCourse(c.itself);
      j <- CourseToJson.toJsonFor(c, approval)
    ) yield j
  }

}