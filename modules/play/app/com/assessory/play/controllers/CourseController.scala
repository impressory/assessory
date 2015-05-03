package com.assessory.play.controllers

import com.wbillingsley.handy.appbase.Course
import com.wbillingsley.handy.appbase.Course.Preenrol
import play.api.mvc.{Action, Controller}
import com.assessory.play.json._
import play.api.mvc.AnyContent

import com.assessory.api._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handyplay.{DataAction, HeaderInfo, WithHeaderInfo}
import com.assessory.model._

import com.assessory.api.wiring.Lookups._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

object CourseController extends Controller {

  implicit val courseToJson = CourseToJson
  implicit val preenrolToJson = PreenrolToJson


  /**
   * Retrieves a course
   */
  def get(id:String) = DataAction.returning.oneWH { implicit request =>
    WithHeaderInfo(
      LazyId(id).of[Course],
      headerInfo
    )
  }

  /**
   * Creates a course
   */
  def create = DataAction.returning.oneWH(parse.json) { implicit request =>
    WithHeaderInfo(
      CourseModel.create(
        a = request.approval,
        json = request.body
      ),
      headerInfo
    )
  }

  /**
   * Retrieves a course
   */
  def findMany = DataAction.returning.manyWH(parse.json) { implicit request =>
    WithHeaderInfo(
      CourseModel.findMany(
        oIds = (request.body \ "ids").asOpt[Set[String]]
      ),
      headerInfo
    )
  }

  def preenrol(preenrolId:String) = DataAction.returning.oneWH { implicit request =>
    WithHeaderInfo(
      LazyId(preenrolId).of[Preenrol],
      headerInfo
    )
  }

  def coursePreenrols(courseId:String) = DataAction.returning.manyWH { implicit request =>
    WithHeaderInfo(
      CourseModel.coursePreenrols(
        a = request.approval,
        rCourse = LazyId(courseId).of[Course]
      ),
      headerInfo
    )
  }

  def createPreenrol(courseId:String) = DataAction.returning.oneWH(parse.json) { implicit request =>
    WithHeaderInfo(
      CourseModel.createPreenrol(
        a = request.approval,
        rCourse = LazyId(courseId).of[Course],
        json = request.body
      ),
      headerInfo
    )
  }

  /**
   * Fetches the courses this user is registered with.
   * Note that this also performs the pre-enrolments
   */
  def myCourses = DataAction.returning.manyJsonWH { implicit request =>
    WithHeaderInfo(
      CourseModel.myCourses(
        rUser = request.user
      ),
      headerInfo
    )
  }


  /**
   * Generates a CSV of the autologin links
   */
  def autolinks(course:Ref[Course]) = DataAction.returning.resultWH { implicit request =>
    val lines = for {
      c <- course.refId
      u <- CourseModel.usersInCourse(request.approval, c)
      studentIdent <- u.getIdentity(I_STUDENT_NUMBER).toRef
      url = routes.UserController.autologin(u.itself, u.secret).absoluteURL()
    } yield s"${studentIdent.value},${u.nickname.getOrElse("")},${url}\n"

    import com.wbillingsley.handyplay.RefConversions._
    WithHeaderInfo(
      for {
        e <- lines.enumerateR
      } yield Ok.chunked(e).as("application/csv"),
      headerInfo
    )

  }

}
