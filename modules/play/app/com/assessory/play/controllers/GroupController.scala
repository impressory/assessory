package com.assessory.play.controllers

import com.wbillingsley.handy.appbase.{Group, Course, GroupSet}
import play.api.mvc.{Action, Controller}
import com.assessory.reactivemongo._
import com.assessory.play.json._
import play.api.mvc.AnyContent

import com.assessory.api._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handyplay.{WithHeaderInfo, DataAction}
import com.assessory.model.GroupModel

import com.assessory.api.wiring.Lookups._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

object GroupController extends Controller {

  implicit val gsToJson = GroupSetToJson
  implicit val gpToJson = GPreenrolToJson
  implicit val groupToJson = GroupToJson


  def groupSet(id:String) = DataAction.returning.oneWH { implicit request =>
    WithHeaderInfo(
      LazyId(id).of[GroupSet],
      headerInfo
    )
  }

  def createGroupSet(courseId:String) = DataAction.returning.oneWH(parse.json) { implicit request =>
    WithHeaderInfo(
      GroupModel.createGroupSet(
        a=request.approval,
        rCourse=LazyId(courseId).of[Course],
        json=request.body
      ),
      headerInfo
    )
  }

  def editGroupSet(gsId:String) = DataAction.returning.oneWH(parse.json) { implicit request =>
    WithHeaderInfo(
      GroupModel.editGroupSet(
        a=request.approval,
        rGS=LazyId(gsId).of[GroupSet],
        json=request.body
      ),
      headerInfo
    )
  }

  /**
   * The group sets in a course
   */
  def courseGroupSets(courseId:String) = DataAction.returning.manyWH { implicit request =>
    WithHeaderInfo(
      GroupModel.courseGroupSets(
        a=request.approval,
        rCourse=LazyId(courseId).of[Course]
      ),
      headerInfo
    )
  }

  /**
   * Creates a group pre-enrolment from submitted CSV data
   * groupName,service,id,username
   */
  def createGroupSetPreenrol(gsId:String) = DataAction.returning.oneWH(parse.json) { implicit request =>
    WithHeaderInfo(
      GroupModel.createGroupSetPreenrol(
        a=request.approval,
        rGS=LazyId(gsId).of[GroupSet],
        oCsv=(request.body \ "csv").asOpt[String]
      ),
      headerInfo
    )
  }

  def testCsv = DataAction.returning.resultWH(parse.tolerantText) { implicit request =>
    import au.com.bytecode.opencsv.CSVReader
    import java.io.StringReader

    import scala.collection.JavaConverters._
    import com.wbillingsley.handyplay.RefConversions._

    val reader = new CSVReader(new StringReader(request.body.trim()))
    val lines = reader.readAll().asScala.toSeq.toRefMany.map(_.toSeq.toString)

    val r = for { e <- lines.enumerateR } yield Ok.chunked(e)
    WithHeaderInfo(
      r,
      headerInfo
    )
  }

  /**
   * Creates groups from submitted CSV data
   */
  def importFromCsv(gsId:String) = DataAction.returning.manyWH(parse.tolerantText) { implicit request =>
    WithHeaderInfo(
      GroupModel.importFromCsv(
        a=request.approval,
        rSet=LazyId(gsId).of[GroupSet],
        csv=request.body
      ),
      headerInfo
    )
  }

  def uploadGroups(gs:Ref[GroupSet]) = DataAction.returning.manyWH(parse.json) { implicit request =>
    WithHeaderInfo(
      (request.body \ "csv").asOpt[String].toRef flatMap { csv =>
        GroupModel.importFromCsv(
          a=request.approval,
          rSet=gs,
          csv=csv
        )
      },
      headerInfo
    )
  }

  /**
   * The groups belonging to a particular group set
   */
  def groupSetGroups(gsId:String) = DataAction.returning.manyWH { implicit request =>
    WithHeaderInfo(
      GroupModel.groupSetGroups(
        a=request.approval,
        rGS=LazyId(gsId).of[GroupSet]
      ),
      headerInfo
    )
  }

  def group(id:String) = DataAction.returning.oneWH { implicit request =>
    WithHeaderInfo(
      LazyId(id).of[Group],
      headerInfo
    )
  }


  def myGroups(courseId:String) = DataAction.returning.manyWH { implicit request =>
    WithHeaderInfo(
      GroupModel.myGroups(
        a=request.approval,
        rCourse=LazyId(courseId).of[Course]
      ),
      headerInfo
    )
  }

  def findMany = DataAction.returning.manyWH(parse.json) { implicit request =>
    WithHeaderInfo(
      GroupModel.findMany(
        oIds=(request.body \ "ids").asOpt[Seq[String]]
      ),
      headerInfo
    )
  }

}
