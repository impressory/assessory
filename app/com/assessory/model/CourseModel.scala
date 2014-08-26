package com.assessory.model;

import com.assessory.reactivemongo._
import com.assessory.play.json._

import com.assessory.api._
import course._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import Ids._
import play.api.libs.json.JsValue

import com.assessory.api.wiring.Lookups._

object CourseModel {


  /**
   * Creates a course
   */
  def create(a:Approval[User], json:JsValue):Ref[Course] = {
    for {
      u <- a.who orIfNone Refused("You must be logged in to create courses")
      approved <- a ask Permissions.CreateCourse;
      unsaved = CourseToJson.update(CourseDAO.unsaved.copy(addedBy=u.itself), json);
      saved <- CourseDAO.saveNew(unsaved);

      reg <- RegistrationDAO.register(u.id, saved.id, CourseRole.roles)
    } yield saved
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
    val updates = for {
      i <- user.identities.toRefMany;
      p <- PreenrolDAO.useRow(service = i.service, value = Some(i.value), username = i.username);

      cId <- p.course.refId
      reg <- RegistrationDAO.register(user.id, cId, p.roles)
    } yield user

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
      r <- RegistrationDAO.byUser(u.id)
    ) yield r.course


    for {
      ids <- rIds.toIds
      c <- ids.lookUp
      approved <- approval ask Permissions.ViewCourse(c.itself);
      j <- CourseToJson.toJsonFor(c, approval)
    } yield j
  }

}