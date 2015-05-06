package com.assessory.play.controllers

import play.api.mvc._
import com.assessory.api._
import com.assessory.clientpickle.Pickles._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handyplay.{WithHeaderInfo, DataAction}
import com.wbillingsley.handyplay.RefConversions._
import com.assessory.api.critique._
import play.api.libs.iteratee.Enumerator
import com.assessory.model.{CritModel, TaskOutputModel}

import com.assessory.api.wiring.Lookups._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.language.implicitConversions

object TaskOutputController extends Controller {

  implicit def taskOutputToResult(rc:Ref[TaskOutput]):Ref[Result] = {
    rc.map(c => Results.Ok(upickle.write(c)).as("application/json"))
  }

  implicit def manyTaskOutputToResult(rc:RefMany[TaskOutput]):Ref[Result] = {
    val strings = rc.map(c => upickle.write(c))

    for {
      enum <- strings.enumerateR
    } yield Results.Ok.chunked(enum.stringifyJsArr).as("application/json")
  }

  def get(id:String) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      LazyId(id).of[TaskOutput],
      headerInfo
    )
  }

  def myOutputs(taskId:String) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      TaskOutputModel.myOutputs(
        a = request.approval,
        rTask = LazyId(taskId).of[Task]
      ),
      headerInfo
    )
  }
  
  def create(taskId:String) = DataAction.returning.resultWH { implicit request =>
    def wp = for {
      text <- request.body.asText.toRef
      client = upickle.read[TaskOutput](text)
      wp <- TaskOutputModel.create(
        a = request.approval,
        task = LazyId(taskId).of[Task],
        clientTaskOutput = client,
        finalise = false // TODO: allow finalising of tasks
      )
    } yield wp

    WithHeaderInfo(wp, headerInfo)
  }
  
  def updateBody(id:String) = DataAction.returning.resultWH { implicit request =>
    def wp = for {
      text <- request.body.asText.toRef
      client = upickle.read[TaskOutput](text)
      wp <- TaskOutputModel.updateBody(
        a = request.approval,
        clientTaskOutput = client,
        finalise = false // TODO: allow finalising of tasks
      )
    } yield wp

    WithHeaderInfo(wp, headerInfo)
  }

}