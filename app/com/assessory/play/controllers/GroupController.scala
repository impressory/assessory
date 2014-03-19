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
import com.wbillingsley.handy.appbase.{WithHeaderInfo, DataAction}
import com.assessory.model.GroupModel

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