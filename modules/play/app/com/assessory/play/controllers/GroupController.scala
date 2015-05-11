package com.assessory.play.controllers

import com.assessory.api.client.WithPerms
import com.assessory.clientpickle.Pickles._
import com.wbillingsley.handy.appbase.{Group, Course, GroupSet}
import play.api.mvc._

import com.assessory.api._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.Id._
import com.wbillingsley.handyplay.{WithHeaderInfo, DataAction}
import com.wbillingsley.handyplay.RefConversions._
import com.assessory.model.GroupModel

import com.assessory.api.wiring.Lookups._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.language.implicitConversions

object GroupController extends Controller {

  implicit def groupToResult(rc:Ref[Group]):Ref[Result] = {
    rc.map(c => Results.Ok(upickle.write(c)).as("application/json"))
  }

  implicit def groupSetToResult(rc:Ref[GroupSet]):Ref[Result] = {
    rc.map(c => Results.Ok(upickle.write(c)).as("application/json"))
  }

  implicit def wpgToResult(rc:Ref[WithPerms[Group]]):Ref[Result] = {
    rc.map(c => Results.Ok(upickle.write(c)).as("application/json"))
  }

  implicit def wpgsToResult(rc:Ref[WithPerms[GroupSet]]):Ref[Result] = {
    rc.map(c => Results.Ok(upickle.write(c)).as("application/json"))
  }

  implicit def manyGroupToResult(rc:RefMany[Group]):Ref[Result] = {
    val strings = rc.map(c => upickle.write(c))

    for {
      enum <- strings.enumerateR
    } yield Results.Ok.chunked(enum.stringifyJsArr).as("application/json")
  }

  implicit def manyGroupSetToResult(rc:RefMany[GroupSet]):Ref[Result] = {
    val strings = rc.map(c => upickle.write(c))

    for {
      enum <- strings.enumerateR
    } yield Results.Ok.chunked(enum.stringifyJsArr).as("application/json")
  }

  implicit def manyWpgToResult(rc:RefMany[WithPerms[Group]]):Ref[Result] = {
    val strings = rc.map(c => upickle.write(c))

    for {
      enum <- strings.enumerateR
    } yield Results.Ok.chunked(enum.stringifyJsArr).as("application/json")
  }

  implicit def manyWpgsToResult(rc:RefMany[WithPerms[GroupSet]]):Ref[Result] = {
    val strings = rc.map(c => upickle.write(c))

    for {
      enum <- strings.enumerateR
    } yield Results.Ok.chunked(enum.stringifyJsArr).as("application/json")
  }


  def groupSet(id:String) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      GroupModel.groupSet(request.approval, id.asId),
      headerInfo
    )
  }

  def createGroupSet(courseId:String) = DataAction.returning.resultWH { implicit request =>
    def wp = for {
      text <- request.body.asText.toRef
      clientGS = upickle.read[GroupSet](text)
      wp <- GroupModel.createGroupSet(request.approval, clientGS)
    } yield wp

    WithHeaderInfo(wp, headerInfo)
  }

  def editGroupSet(gsId:String) = DataAction.returning.resultWH { implicit request =>
    def wp = for {
      text <- request.body.asText.toRef
      clientGS = upickle.read[GroupSet](text)
      wp <- GroupModel.editGroupSet(request.approval, clientGS)
    } yield wp

    WithHeaderInfo(wp, headerInfo)
  }

  /**
   * The group sets in a course
   */
  def courseGroupSets(courseId:String) = DataAction.returning.resultWH { implicit request =>
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
   *
  def createGroupSetPreenrol(gsId:String) = DataAction.returning.oneWH(parse.json) { implicit request =>
    WithHeaderInfo(
      GroupModel.createGroupSetPreenrol(
        a=request.approval,
        rGS=LazyId(gsId).of[GroupSet],
        oCsv=(request.body \ "csv").asOpt[String]
      ),
      headerInfo
    )
  }*/

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
   *
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
  }*/

  /**
   * The groups belonging to a particular group set
   */
  def groupSetGroups(gsId:String) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      GroupModel.groupSetGroups(
        a=request.approval,
        rGS=LazyId(gsId).of[GroupSet]
      ),
      headerInfo
    )
  }

  def group(id:String) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      GroupModel.group(request.approval, id.asId),
      headerInfo
    )
  }


  def myGroups(courseId:String) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      GroupModel.myGroups(
        a=request.approval,
        rCourse=LazyId(courseId).of[Course]
      ),
      headerInfo
    )
  }

  def findMany = DataAction.returning.resultWH { implicit request =>
    def wp = for {
      text <- request.body.asText.toRef
      ids = upickle.read[Ids[Group,String]](text)
      wp <- GroupModel.findMany(request.approval, ids)
    } yield wp

    WithHeaderInfo(wp, headerInfo)
  }
}
