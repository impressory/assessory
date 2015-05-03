package com.assessory.play.controllers

import play.api.mvc.Controller
import com.assessory.asyncmongo._
import com.assessory.play.json._
import com.assessory.api._
import critique._
import com.wbillingsley.handy._
import com.wbillingsley.handyplay.{WithHeaderInfo, DataAction}

import com.assessory.api.wiring.Lookups._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import com.assessory.model._

object CritController extends Controller {


  implicit val caToJson = CritAllocationToJson
  implicit val ctToJson = CritiqueJson.CTFormat
  implicit val toToJSON = TaskOutputToJson


  def allocateTask(taskId:String) = DataAction.returning.manyWH { implicit request =>
    WithHeaderInfo(
      CritModel.allocateTask(
        a = request.approval,
        rTask = LazyId(taskId).of[Task]
      ),
      headerInfo
    )
  }


  def myAllocation(taskId:String) = DataAction.returning.manyJsonWH { implicit request =>
    WithHeaderInfo(
      for {
        t <- CritModel.myAllocations(request.approval, LazyId(taskId).of[Task])
      } yield CritiqueJson.CTFormat.writes(t),
      headerInfo
    )
  }

  def allocations(taskId:String) = DataAction.returning.manyWH { implicit request =>
    WithHeaderInfo(
      CritAllocationDAO.byTask(LazyId(taskId).of[Task]),
      headerInfo
    )
  }

  def findOrCreateCrit(taskId:String) = DataAction.returning.oneWH(parse.json) { implicit request =>
    WithHeaderInfo(
      CritModel.findOrCreateCrit(
        a = request.approval,
        rTask = LazyId(taskId).of[Task],
        target = request.body.as[CritTarget]
      ),
      headerInfo
    )
  }

  /** Fetches allocations as a CSV. */
  def allocationsAsCSV(taskId:String) = DataAction.returning.resultWH { implicit request =>
    import com.wbillingsley.handyplay.RefConversions._

    val lines = CritModel.allocationsAsCSV(
      a = request.approval,
      rTask = LazyId(taskId).of[Task]
    ).enumerateR

    WithHeaderInfo(
      lines.map(Ok.chunked(_).as("application/csv")),
      headerInfo
    )
  }
}
