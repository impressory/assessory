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
import com.wbillingsley.handyplay.{WithHeaderInfo, DataAction}
import com.assessory.api.critique._
import play.api.libs.iteratee.Enumerator
import com.assessory.model.TaskOutputModel

import com.assessory.api.wiring.Lookups._

object TaskOutputController extends Controller {

  implicit val toToJSON = TaskOutputToJson
  
  def get(id:String) = DataAction.returning.oneWH { implicit request =>
    WithHeaderInfo(
      LazyId(id).of[TaskOutput],
      headerInfo
    )
  }

  def relevantToMe(taskId:String) = DataAction.returning.manyWH { implicit request =>
    WithHeaderInfo(
      TaskOutputModel.relevantToMe(
        a = request.approval,
        rTask = LazyId(taskId).of[Task]
      ),
      headerInfo
    )
  }
  
  def myOutputs(taskId:String) = DataAction.returning.manyWH { implicit request =>
    WithHeaderInfo(
      TaskOutputModel.myOutputs(
        a = request.approval,
        rTask = LazyId(taskId).of[Task]
      ),
      headerInfo
    )
  }
  
  def create(taskId:String) = DataAction.returning.oneWH(parse.json) { implicit request =>
    WithHeaderInfo(
      TaskOutputModel.create(
        a = request.approval,
        rTask = LazyId(taskId).of[Task],
        json = request.body
      ),
      headerInfo
    )
  }
  
  def updateBody(id:String) = DataAction.returning.jsonWH(parse.json) { implicit request =>
    WithHeaderInfo(
      TaskOutputModel.updateBody(
        a = request.approval,
        rTaskOutput = LazyId(id).of[TaskOutput],
        json = request.body
      ),
      headerInfo
    )
  }
  
  def asCsv(taskId:String) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      {
        for {
          en <- TaskOutputModel.asCsv(
            a = request.approval,
            rTask = LazyId(taskId).of[Task]
          )
        } yield Ok.chunked(en)
      },
      headerInfo
    )
  }
  
}