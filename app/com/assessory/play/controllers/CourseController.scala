package com.assessory.play.controllers

import play.api.mvc.{Action, Controller}
import com.assessory.reactivemongo._
import com.assessory.play.json._
import play.api.mvc.AnyContent

import com.assessory.api._
import course._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.DataAction

object CourseController extends Controller {
  
  implicit val courseToJson = CourseToJson
  implicit val preenrolToJson = PreenrolToJson
  
  val dataAction = new DataAction
  
  def refCourse(id:String) = new LazyId(classOf[Course], id)
  def refPreenrol(id:String) = new LazyId(classOf[Preenrol], id)
  
  /**
   * Retrieves a course
   */  
  def get(id:String) = dataAction.one { implicit request =>     
    val cache = request.approval.cache
    for (
      course <- cache(refCourse(id));
      approved <- request.approval ask Permissions.ViewCourse(course.itself)
    ) yield course
  }
  
  /**
   * Creates a course
   */  
  def create = dataAction.one(parse.json) { implicit request =>
    val cache = request.approval.cache
    for (
      approved <- request.approval ask Permissions.CreateCourse;
      unsaved = CourseToJson.update(CourseDAO.unsaved.copy(addedBy=request.user), request.body);
      saved <- CourseDAO.saveNew(unsaved);
      regPushed <- UserDAO.pushRegistration(request.user, Registration(course=saved.itself, roles=Seq(CourseRole.student, CourseRole.staff)))
    ) yield saved
  }
  
  /**
   * Retrieves a course
   */  
  def findMany = dataAction.many(parse.json) { implicit request =>
    
    val ids = (request.body \ "ids").asOpt[Set[String]].getOrElse(Set.empty)
    val cache = request.approval.cache
    
    for (
      course <- new RefManyById(classOf[Course], ids.toSeq);
      approved <- request.approval ask Permissions.ViewCourse(course.itself)
    ) yield course
  }
  
  def preenrol(preenrolId:String) = dataAction.one { implicit request => 
    for (
      preenrol <- request.approval.cache(refPreenrol(preenrolId));
      approved <- request.approval ask Permissions.EditCourse(preenrol.course)
    ) yield preenrol
  }
  
  def coursePreenrols(courseId:String) = dataAction.many { implicit request => 
    for (
      course <- request.approval.cache(refCourse(courseId));
      approved <- request.approval ask Permissions.ViewCourse(course.itself);
      preenrol <- PreenrolDAO.byCourse(course.itself)
    ) yield preenrol
  }
  
  def createPreenrol(courseId:String) = dataAction.one(parse.json) { implicit request =>
    val cache = request.approval.cache
    for (
      course <- refCourse(courseId);
      approved <- request.approval ask Permissions.EditCourse(course.itself);
      unsaved <- PreenrolToJson.updateWithCsv(PreenrolDAO.unsaved.copy(course=course.itself), request.body);
      saved <- PreenrolDAO.saveNew(unsaved)
    ) yield saved
  }  
  
}