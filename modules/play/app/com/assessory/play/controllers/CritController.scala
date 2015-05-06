package com.assessory.play.controllers

import com.assessory.api.client.WithPerms
import play.api.mvc.{Results, Result, Controller}
import com.assessory.api._
import com.assessory.clientpickle.Pickles._
import critique._
import com.wbillingsley.handy._
import Ref._
import com.wbillingsley.handyplay.{WithHeaderInfo, DataAction}
import com.wbillingsley.handyplay.RefConversions._

import com.assessory.api.wiring.Lookups._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.language.implicitConversions

import com.assessory.model._

object CritController extends Controller {

  implicit def caToResult(rc:Ref[CritAllocation]):Ref[Result] = {
    rc.map(c => Results.Ok(upickle.write(c)).as("application/json"))
  }

  implicit def taskOutputToResult(rc:Ref[TaskOutput]):Ref[Result] = {
    rc.map(c => Results.Ok(upickle.write(c)).as("application/json"))
  }


  implicit def targetToResult(rc:Ref[Target]):Ref[Result] = {
    rc.map(c => Results.Ok(upickle.write(c)).as("application/json"))
  }


  implicit def manyCAToResult(rc:RefMany[CritAllocation]):Ref[Result] = {
    val strings = rc.map(c => upickle.write(c))

    for {
      enum <- strings.enumerateR
    } yield Results.Ok.chunked(enum.stringifyJsArr).as("application/json")
  }

  implicit def manyTargetToResult(rc:RefMany[Target]):Ref[Result] = {
    val strings = rc.map(c => upickle.write(c))

    for {
      enum <- strings.enumerateR
    } yield Results.Ok.chunked(enum.stringifyJsArr).as("application/json")
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


  def allocateTask(taskId:String) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      CritModel.allocateTask(
        a = request.approval,
        rTask = LazyId(taskId).of[Task]
      ),
      headerInfo
    )
  }


  def myAllocation(taskId:String) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      CritModel.myAllocations(request.approval, LazyId(taskId).of[Task]),
      headerInfo
    )
  }

  def allocations(taskId:String) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      CritModel.allocations(LazyId(taskId).of[Task]),
      headerInfo
    )
  }

  def findOrCreateCrit(taskId:String) = DataAction.returning.resultWH { implicit request =>
    def wp = for {
      text <- request.body.asText.toRef
      client = upickle.read[Target](text)
      wp <- CritModel.findOrCreateCrit(
        a = request.approval,
        rTask = LazyId(taskId).of[Task],
        target = client
      )
    } yield wp

    WithHeaderInfo(wp, headerInfo)
  }

  /** Fetches allocations as a CSV. */
  def allocationsAsCSV(taskId:String) = DataAction.returning.resultWH { implicit request =>
    val lines = CritModel.allocationsAsCSV(
      a = request.approval,
      rTask = LazyId(taskId).of[Task]
    )

    WithHeaderInfo(
      lines.map(Ok(_).as("application/csv")),
      headerInfo
    )
  }
}
