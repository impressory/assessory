package com.assessory.play.controllers

import play.api.mvc.{Action, Controller}
import com.assessory.reactivemongo._
import com.assessory.play.json._
import com.assessory.model._

import com.assessory.api._
import course._
import group._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handyplay.{WithHeaderInfo, DataAction}

import com.assessory.api.wiring.Lookups._

object TaskController extends Controller {
  
  implicit val taskToJson = TaskToJson

  def get(id:String) = DataAction.returning.oneWH { implicit request =>
    WithHeaderInfo(
      LazyId(id).of[Task],
      headerInfo
    )
  }
  
  
  def create(courseId:String) = DataAction.returning.oneWH(parse.json) { implicit request =>
    WithHeaderInfo(
      TaskModel.create(
        a = request.approval,
        rCourse = LazyId(courseId).of[Course],
        json = request.body
      ),
      headerInfo
    )
  }
  
  def updateBody(taskId:String) = DataAction.returning.oneWH(parse.json) { implicit request =>
    WithHeaderInfo(
      TaskModel.updateBody(
        a = request.approval,
        rTask = LazyId(taskId).of[Task],
        json = request.body
      ),
      headerInfo
    )
  }
  
  def courseTasks(courseId:String) = DataAction.returning.manyWH { implicit request =>
    WithHeaderInfo(
      TaskModel.courseTasks(
        a = request.approval,
        rCourse = LazyId(courseId).of[Course]
      ),
      headerInfo
    )
  }

}