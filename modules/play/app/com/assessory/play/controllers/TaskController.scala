package com.assessory.play.controllers

import com.assessory.api.client.WithPerms
import com.wbillingsley.handy.appbase.{GroupSet, Group, Course}
import play.api.mvc.{Results, Result, Action, Controller}
import com.assessory.model._

import com.assessory.api._
import com.assessory.clientpickle.Pickles._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handyplay.{WithHeaderInfo, DataAction}
import com.wbillingsley.handyplay.RefConversions._

import com.assessory.api.wiring.Lookups._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.language.implicitConversions

object TaskController extends Controller {

  implicit def taskToResult(rc:Ref[Task]):Ref[Result] = {
    rc.map(c => Results.Ok(upickle.write(c)).as("application/json"))
  }

  implicit def wptToResult(rc:Ref[WithPerms[Task]]):Ref[Result] = {
    rc.map(c => Results.Ok(upickle.write(c)).as("application/json"))
  }

  implicit def manyTaskToResult(rc:RefMany[Task]):Ref[Result] = {
    val strings = rc.map(c => upickle.write(c))

    for {
      enum <- strings.enumerateR
    } yield Results.Ok.chunked(enum.stringifyJsArr).as("application/json")
  }

  implicit def manyWptToResult(rc:RefMany[WithPerms[Task]]):Ref[Result] = {
    val strings = rc.map(c => upickle.write(c))

    for {
      enum <- strings.enumerateR
    } yield Results.Ok.chunked(enum.stringifyJsArr).as("application/json")
  }

  def get(id:String) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      LazyId(id).of[Task],
      headerInfo
    )
  }
  
  
  def create(courseId:String) = DataAction.returning.resultWH { implicit request =>
    def wp = for {
      text <- request.body.asText.toRef
      client = upickle.read[Task](text)
      wp <- TaskModel.create(request.approval, client)
    } yield wp

    WithHeaderInfo(wp, headerInfo)
  }
  
  def updateBody(taskId:String) = DataAction.returning.resultWH { implicit request =>
    def wp = for {
      text <- request.body.asText.toRef
      client = upickle.read[Task](text)
      wp <- TaskModel.updateBody(request.approval, client)
    } yield wp

    WithHeaderInfo(wp, headerInfo)
  }
  
  def courseTasks(courseId:String) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      TaskModel.courseTasks(
        a = request.approval,
        rCourse = LazyId(courseId).of[Course]
      ),
      headerInfo
    )
  }

}