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
  
  def refCourse(id:String) = LazyId(id).of[Course]
  def refPreenrol(id:String) = LazyId(id).of[Preenrol]
  
  /**
   * Retrieves a course
   */  
  def get(id:String) = DataAction.returning.one { implicit request =>     
    for (
      course <- refCourse(id);
      approved <- request.approval ask Permissions.ViewCourse(course.itself)
    ) yield course
  }
  
  /**
   * Creates a course
   */  
  def create = DataAction.returning.result(parse.json) { implicit request =>
    val cache = request.approval.cache
    for {
      u <- request.user
      approved <- request.approval ask Permissions.CreateCourse;
      unsaved = CourseToJson.update(CourseDAO.unsaved.copy(addedBy=u.itself), request.body);
      saved <- CourseDAO.saveNew(unsaved);
      regPushed <- UserDAO.pushRegistration(request.user, Registration(course=saved.itself, roles=Seq(CourseRole.student, CourseRole.staff)));
      
      // When the request began, the user was not registered with this course. We need to produce json for the updated version that is
      j <- CourseToJson.toJsonFor(saved, Approval(regPushed.itself))
    } yield Ok(j)
  }
  
  /**
   * Retrieves a course
   */  
  def findMany = DataAction.returning.many(parse.json) { implicit request =>
    
    val ids = (request.body \ "ids").asOpt[Set[String]].getOrElse(Set.empty)
    val cache = request.approval.cache
    
    for (
      course <- RefManyById(ids.toSeq).of[Course];
      approved <- request.approval ask Permissions.ViewCourse(course.itself)
    ) yield course
  }
  
  def preenrol(preenrolId:String) = DataAction.returning.one { implicit request => 
    for (
      preenrol <- request.approval.cache(refPreenrol(preenrolId));
      approved <- request.approval ask Permissions.EditCourse(preenrol.course)
    ) yield preenrol
  }
  
  def coursePreenrols(courseId:String) = DataAction.returning.many { implicit request => 
    for (
      course <- request.approval.cache(refCourse(courseId));
      approved <- request.approval ask Permissions.ViewCourse(course.itself);
      preenrol <- PreenrolDAO.byCourse(course.itself)
    ) yield preenrol
  }
  
  def createPreenrol(courseId:String) = DataAction.returning.one(parse.json) { implicit request =>
    val cache = request.approval.cache
    for (
      course <- refCourse(courseId);
      approved <- request.approval ask Permissions.EditCourse(course.itself);
      unsaved <- PreenrolToJson.updateWithCsv(PreenrolDAO.unsaved.copy(course=course.itself), request.body);
      saved <- PreenrolDAO.saveNew(unsaved)
    ) yield saved
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
  def myCourses = DataAction.returning.manyJson { implicit request =>
    val userAfterUpdates = for (u <- request.user; updated <- doPreenrolments(u)) yield updated
    
    // As we've updated the user, we'll need a new Approval
    val approval = Approval(userAfterUpdates)

    val rIds = for (
      u <- userAfterUpdates;
      r <- u.registrations.toRefMany;
      cid <- Ref(r.course.getId)
    ) yield cid
    
    
    for (
      ids <- rIds.toRefOne;
      c <- RefManyById(ids.toSeq).of[Course];
      approved <- approval ask Permissions.ViewCourse(c.itself);
      j <- CourseToJson.toJsonFor(c, approval)
    ) yield j
  }
  
}