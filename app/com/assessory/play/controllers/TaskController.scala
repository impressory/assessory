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

object TaskController extends Controller {
  
  implicit val taskToJson = TaskToJson
  
  def refTask(id:String) = new LazyId(classOf[Task], id)
  def refCourse(id:String) = new LazyId(classOf[Course], id)
  
  def get(id:String) = DataAction.returning.one { implicit request => 
    for (
      t <- refTask(id);
      approved <- request.approval ask Permissions.ViewCourse(t.course)
    ) yield t
  }
  
  
  def create(courseId:String) = DataAction.returning.one(parse.json) { implicit request =>
    for (
      c <- refCourse(courseId);
      approved <- request.approval ask Permissions.EditCourse(c.itself);
      t = TaskToJson.update(TaskDAO.unsaved.copy(course=c.itself), request.body);
      saved <- TaskDAO.saveNew(t)
    ) yield saved  
  }
  
  def updateBody(taskId:String) = DataAction.returning.one(parse.json) { implicit request =>
    for (
      t <- refTask(taskId);
      a <- request.approval ask Permissions.EditTask(t.itself);
      updated = TaskToJson.update(t, request.body);
      saved <- TaskDAO.updateBody(updated)
    ) yield saved
  }
  
  def courseTasks(courseId:String) = DataAction.returning.many { implicit request => 
    for (
      c <- refCourse(courseId);
      approved <- request.approval ask Permissions.ViewCourse(c.itself);
      t <- TaskDAO.byCourse(c.itself)
    ) yield t
  }

}