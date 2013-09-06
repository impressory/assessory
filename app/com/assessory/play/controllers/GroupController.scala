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

object GroupController extends Controller {
  
  implicit val gsToJson = GroupSetToJson
  implicit val gpToJson = GPreenrolToJson
  implicit val groupToJson = GroupToJson
  
  def refCourse(id:String) = new LazyId(classOf[Course], id)
  
  def refGroupSet(id:String) = new LazyId(classOf[GroupSet], id)
  
  def refGroup(id:String) = new LazyId(classOf[Group], id)

  def groupSet(id:String) = DataAction.returning.one { implicit request =>     
    val cache = request.approval.cache
    for (
      gs <- cache(refGroupSet(id));
      approved <- request.approval ask Permissions.ViewCourse(gs.course)
    ) yield gs
  }

  def createGroupSet(courseId:String) = DataAction.returning.one(parse.json) { implicit request =>
    val cache = request.approval.cache
    for (
      course <- refCourse(courseId);
      approved <- request.approval ask Permissions.EditCourse(course.itself);
      unsaved = GroupSetToJson.update(GroupSetDAO.unsaved.copy(course=course.itself), request.body);
      saved <- GroupSetDAO.saveNew(unsaved)
    ) yield saved
  }
  
  def editGroupSet(gsId:String) = DataAction.returning.one(parse.json) { implicit request =>
    val cache = request.approval.cache
    for (
      gs <- refGroupSet(gsId);
      approved <- request.approval ask Permissions.EditCourse(gs.course);
      updated = GroupSetToJson.update(gs, request.body);
      saved <- GroupSetDAO.saveDetails(updated)
    ) yield saved
  }

  /**
   * The group sets in a course
   */
  def courseGroupSets(courseId:String) = DataAction.returning.many { implicit request => 
    val cache = request.approval.cache
    for (
      course <- refCourse(courseId);
      approved <- request.approval ask Permissions.ViewCourse(course.itself);
      gs <- GroupSetDAO.byCourse(course.itself)
    ) yield gs
  }
  
  /**
   * Creates a group pre-enrolment from submitted CSV data
   * groupName,service,id,username
   */
  def createGroupSetPreenrol(gsId:String) = DataAction.returning.one(parse.json) { implicit request =>
    for (
      gs <- refGroupSet(gsId);
      approved <- request.approval ask Permissions.EditCourse(gs.course);
      csv <- Ref((request.body \ "csv").asOpt[String]) orIfNone UserError("No CSV data");
      unsaved <- gpreenrolFromCsv(course=gs.course, set=gs.itself, csv=csv);
      saved <- GPreenrolDAO.saveNew(unsaved)
    ) yield saved    
  }
  
  /**
   * The groups belonging to a particular group set
   */
  def groupSetGroups(gsId:String) = DataAction.returning.many { implicit request =>
    for (
      g <- GroupDAO.bySet(refGroupSet(gsId));
      approved <- request.approval ask Permissions.ViewCourse(g.course)
    ) yield g
  }
  
  def group(id:String) = DataAction.returning.one { implicit request =>     
    val cache = request.approval.cache
    for (
      gs <- cache(refGroup(id));
      approved <- request.approval ask Permissions.ViewCourse(gs.course)
    ) yield gs
  }
  
  
  def myGroups(courseId:String) = DataAction.returning.many { implicit request =>
    val course = refCourse(courseId)
    for (
      newlyPreenrolled <- doPreenrolments(course, request.user).toRefOne;
      group <- GroupDAO.byCourseAndUser(course, request.user)
    ) yield group
  }
  
  /**
   * Searches for course pre-enrolments, and performs them
   */
  def doPreenrolments(course:Ref[Course], user:Ref[User]):RefMany[Group]= {
    for (
      u <- user;
      i <- u.identities.toRefMany;
      gPreenrolRow <- GPreenrolDAO.useRow(course, service=i.service, value=i.value, username=i.username);
      g <- GroupDAO.addMember(gPreenrolRow.group, u.itself)
    ) yield g
  }
  
  /**
   * Creates a group pre-enrolment from submitted CSV data
   * groupName,service,id,username
   */
  def gpreenrolFromCsv(course: Ref[Course], set:Ref[GroupSet], csv:String):Ref[GPreenrol] = {
    import au.com.bytecode.opencsv.CSVReader
    import java.io.StringReader
    
    import scala.collection.JavaConverters._
    
    val reader = new CSVReader(new StringReader(csv.trim()))
    val lines = reader.readAll().asScala.toSeq
    reader.close()
    
    val rSeq = try {
      
      val names = lines.map(_(0)).toSet
      val pairs = for (
        name <- names.toRefMany;
        group = GroupDAO.unsaved.copy(name=Some(name), course=course, set=set, provenance=Some("pre-enrol"));
        saved <- GroupDAO.saveNew(group)
      ) yield name -> saved
      val rMap = for (p <- pairs.toRefOne) yield Map(p.toSeq:_*)
      
      val rows = for (
        map <- rMap;
        line <- lines.toRefMany
      ) yield GPreenrolPair(group=map(line(0)).itself, service=line(1), value=line(2), username=line(3))
      rows.toRefOne
    } catch {
      case ex:Throwable => RefFailed(ex)
    }
    
    for (seq <- rSeq) yield GPreenrolDAO.unsaved.copy(course=course, set=set, groupData=seq.toSeq)
  }
  
  
}