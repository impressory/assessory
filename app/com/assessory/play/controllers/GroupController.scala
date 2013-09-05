package com.assessory.play.controllers

import play.api.mvc.{Action, Controller}
import com.assessory.reactivemongo._
import com.assessory.play.json._
import play.api.mvc.AnyContent

import com.assessory.api._
import course._
import group._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.DataAction

object GroupController extends Controller {
  
  implicit val gsToJson = GroupSetToJson
  implicit val gpToJson = GPreenrolToJson
  
  val dataAction = new DataAction
  
  def refCourse(id:String) = new LazyId(classOf[Course], id)
  
  def refGroupSet(id:String) = new LazyId(classOf[GroupSet], id)
  
  def refGroup(id:String) = new LazyId(classOf[Group], id)

  def groupSet(id:String) = dataAction.one { implicit request =>     
    val cache = request.approval.cache
    for (
      gs <- cache(refGroupSet(id));
      approved <- request.approval ask Permissions.ViewCourse(gs.course)
    ) yield gs
  }

  def createGroupSet(courseId:String) = dataAction.one(parse.json) { implicit request =>
    val cache = request.approval.cache
    for (
      course <- refCourse(courseId);
      approved <- request.approval ask Permissions.EditCourse(course.itself);
      unsaved = GroupSetToJson.update(GroupSetDAO.unsaved.copy(course=course.itself), request.body);
      saved <- GroupSetDAO.saveNew(unsaved)
    ) yield saved
  }
  
  def editGroupSet(gsId:String) = dataAction.one(parse.json) { implicit request =>
    val cache = request.approval.cache
    for (
      gs <- refGroupSet(gsId);
      approved <- request.approval ask Permissions.EditCourse(gs.course);
      updated = GroupSetToJson.update(gs, request.body);
      saved <- GroupSetDAO.saveDetails(updated)
    ) yield saved
  }

  def courseGroupSets(courseId:String) = dataAction.many { implicit request => 
    val cache = request.approval.cache
    for (
      course <- refCourse(courseId);
      approved <- request.approval ask Permissions.ViewCourse(course.itself);
      gs <- GroupSetDAO.byCourse(course.itself)
    ) yield gs
  }
  
  def createGroupSetPreenrol(gsId:String) = dataAction.one(parse.json) { implicit request =>
    for (
      gs <- refGroupSet(gsId);
      approved <- request.approval ask Permissions.EditCourse(gs.course);
      unsaved <- GPreenrolToJson.updateWithCsv(GPreenrolDAO.unsaved.copy(course=gs.course, set=gs.itself), request.body);
      saved <- GPreenrolDAO.saveNew(unsaved)
    ) yield saved    
  }

}