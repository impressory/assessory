package com.assessory.play.controllers

import play.api.mvc.{Action, Controller}
import com.assessory.reactivemongo._
import com.assessory.play.json._
import play.api.mvc.AnyContent

import com.assessory.api._
import course._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.{DataAction, HeaderInfo, WithHeaderInfo}
import com.assessory.model._

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
  def create = DataAction.returning.jsonWH(parse.json) { implicit request =>
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
  def myCourses = DataAction.returning.manyJsonWH { implicit request =>
    WithHeaderInfo(
      CourseModel.myCourses(
        rUser = request.user
      ),
      headerInfo
    )
  }
  
}