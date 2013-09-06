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
  
  def get(id:String) = DataAction.returning.one { implicit request => 
    for (
      t <- refTask(id);
      approved <- request.approval ask Permissions.ViewCourse(t.course)
    ) yield t
  }

}