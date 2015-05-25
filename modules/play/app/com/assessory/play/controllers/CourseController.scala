package com.assessory.play.controllers

import com.assessory.api._
import com.assessory.api.client.{CreateCoursePreenrolData, WithPerms}
import com.assessory.api.wiring.Lookups._
import com.assessory.clientpickle.Pickles._
import com.assessory.model._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.Course
import com.wbillingsley.handyplay.RefConversions._
import com.wbillingsley.handyplay.{DataAction, WithHeaderInfo}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{BodyParsers, Controller, Result, Results}

import scala.language.implicitConversions

object CourseController extends Controller {


  implicit def courseToResult(rc:Ref[Course]):Ref[Result] = {
    rc.map(c => Results.Ok(upickle.write(c)).as("application/json"))
  }

  implicit def wpcToResult(rc:Ref[WithPerms[Course]]):Ref[Result] = {
    rc.map(c => Results.Ok(upickle.write(c)).as("application/json"))
  }

  implicit def manyCourseToResult(rc:RefMany[Course]):Ref[Result] = {
    val strings = rc.map(c => upickle.write(c))

    for {
      enum <- strings.enumerateR
    } yield Results.Ok.chunked(enum.stringifyJsArr).as("application/json")
  }

  implicit def manyWpcToResult(rc:RefMany[WithPerms[Course]]):Ref[Result] = {
    val strings = rc.map(c => upickle.write(c))

    for {
      enum <- strings.enumerateR
    } yield Results.Ok.chunked(enum.stringifyJsArr).as("application/json")
  }

  implicit def preenrolToResult(rc:Ref[Course.Preenrol]):Ref[Result] = {
    rc.map(c => Results.Ok(upickle.write(c)).as("application/json"))
  }

  implicit def manyPreenrolToResult(rc:RefMany[Course.Preenrol]):Ref[Result] = {
    val strings = rc.map(c => upickle.write(c))

    for {
      enum <- strings.enumerateR
    } yield Results.Ok.chunked(enum.stringifyJsArr).as("application/json")
  }

  /**
   * Retrieves a course
   */
  def get(id:Id[Course,String]) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      CourseModel.byId(request.approval, id),
      headerInfo
    )
  }

  /**
   * Creates a course
   */
  def create = DataAction.returning.resultWH { implicit request =>
    def wp = for {
      text <- request.body.asText.toRef
      clientCourse = upickle.read[Course](text)
      wp <- CourseModel.create(request.approval, clientCourse)
    } yield wp

    WithHeaderInfo(wp, headerInfo)
  }

  /**
   * Retrieves a course
   */
  def findMany = DataAction.returning.resultWH { implicit request =>
    def wp = for {
      text <- request.body.asText.toRef
      ids = upickle.read[Ids[Course,String]](text)
      wp <- CourseModel.findMany(request.approval, ids)
    } yield wp

    WithHeaderInfo(wp, headerInfo)
  }

  def preenrol(preenrolId:Id[Course.Preenrol,String]) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      preenrolId.lazily,
      headerInfo
    )
  }

  def coursePreenrols(courseId:Id[Course,String]) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      CourseModel.coursePreenrols(
        a = request.approval,
        rCourse = courseId.lazily
      ),
      headerInfo
    )
  }

  def createPreenrol(courseId:Id[Course,String]) = DataAction.returning.resultWH { implicit request =>
    def wp = for {
      text <- request.body.asText.toRef
      data = upickle.read[CreateCoursePreenrolData](text)
      wp <- CourseModel.createPreenrol(request.approval, data.name, data.course, data.roles, data.csv)
    } yield wp

    WithHeaderInfo(wp, headerInfo)
  }

  /**
   * Fetches the courses this user is registered with.
   * Note that this also performs the pre-enrolments
   */
  def myCourses = DataAction.returning.resultWH(BodyParsers.parse.empty) { implicit request =>
    WithHeaderInfo(
      CourseModel.myCourses(request.approval),
      headerInfo
    )
  }


  /**
   * Generates a CSV of the autologin links
   */
  def autolinks(course:Id[Course,String]) = DataAction.returning.resultWH { implicit request =>
    val lines = for {
      u <- CourseModel.usersInCourse(request.approval, course)
      optStudentIdent <- u.getIdentity(I_STUDENT_NUMBER).toRef
      studentIdent <- optStudentIdent.value.toRef
      url = routes.UserController.autologin(u.id, u.secret).absoluteURL()
    } yield s"${studentIdent},$url\n"

    import com.wbillingsley.handyplay.RefConversions._
    WithHeaderInfo(
      for {
        e <- lines.enumerateR
      } yield Ok.chunked(e).as("application/csv"),
      headerInfo
    )

  }

}
