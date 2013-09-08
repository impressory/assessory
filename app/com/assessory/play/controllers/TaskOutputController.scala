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


object TaskOutputController extends Controller {

  def refOutput(id:String) = new LazyId(classOf[TaskOutput], id)
  def refCourse(id:String) = new LazyId(classOf[Course], id)
  
  implicit val toToJSON = TaskOutputToJson
  
  def get(id:String) = DataAction.returning.one { implicit request => 
    refOutput(id)
  }
  
  def editBody(id:String) = DataAction.returning.one(parse.json) { implicit request => 
    for (
      output <- refOutput(id);
      approved <- request.approval ask Permissions.EditOutput(output.itself);
      updated = TaskOutputToJson.update(output, request.body);
      saved <- TaskOutputDAO.updateBody(updated)
    ) yield saved
  }
  
}