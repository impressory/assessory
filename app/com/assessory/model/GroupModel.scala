package com.assessory.model

import com.assessory.reactivemongo._
import com.assessory.play.json._

import com.assessory.api._
import course._
import group._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.Ids._
import play.api.libs.json.JsValue

import com.assessory.api.wiring.Lookups._

object GroupModel {


  def createGroupSet(a:Approval[User], rCourse:Ref[Course], json:JsValue) = {
    for {
      course <- rCourse
      approved <- a ask Permissions.EditCourse(course.itself)
      unsaved = GroupSetToJson.update(GroupSetDAO.unsaved.copy(course=course.itself), json)
      saved <- GroupSetDAO.saveNew(unsaved)
    } yield saved
  }

  def editGroupSet(a:Approval[User], rGS:Ref[GroupSet], json:JsValue) = {
    for (
      gs <- rGS;
      approved <- a ask Permissions.EditGroupSet(gs.itself);
      updated = GroupSetToJson.update(gs, json);
      saved <- GroupSetDAO.saveDetails(updated)
    ) yield saved
  }

  /**
   * The group sets in a course
   */
  def courseGroupSets(a:Approval[User], rCourse:Ref[Course]) = {
    for (
      course <- rCourse;
      approved <- a ask Permissions.ViewCourse(course.itself);
      gs <- GroupSetDAO.byCourse(course.itself)
    ) yield gs
  }

  /**
   * Creates a group pre-enrolment from submitted CSV data
   * groupName,service,id,username
   */
  def createGroupSetPreenrol(a:Approval[User], rGS:Ref[GroupSet], oCsv:Option[String]) = {
    for (
      gs <- rGS;
      approved <- a ask Permissions.EditGroupSet(gs.itself);
      csv <- Ref(oCsv) orIfNone UserError("No CSV data");
      unsaved <- gpreenrolFromCsv(course=gs.course, set=gs.itself, csv=csv);
      gsUpdated <- GroupSetDAO.setPreenrol(gs.itself, unsaved.itself);
      saved <- GPreenrolDAO.saveNew(unsaved)
    ) yield saved
  }

  /**
   * The groups belonging to a particular group set
   */
  def groupSetGroups(a:Approval[User], rGS:Ref[GroupSet]) = {
    for {
      gs <- rGS
      approved <- a ask Permissions.ViewGroupSet(gs.itself)
      g <- GroupDAO.bySet(gs.itself)
    } yield g
  }


  def myGroups(a:Approval[User], rCourse:Ref[Course]) = {
    for {
      course <- rCourse
      newlyPreenrolled <- doPreenrolments(course.itself, a.who).toRefOne
      group <- GroupDAO.byCourseAndUser(course.itself, a.who)
    } yield group
  }

  def findMany(oIds:Option[Seq[String]]) = {
    for (
      ids <- Ref(oIds) orIfNone UserError("No ids requested");
      g <- RefManyById(ids).of[Group]
    ) yield g
  }

  /**
   * Searches for course pre-enrolments, and performs them
   */
  def doPreenrolments(course:RefWithId[Course], user:Ref[User]):RefMany[Group]= {
    for (
      u <- user;
      i <- u.identities.toRefMany;
      g <- GPreenrolDAO.useRow(course, service=i.service, value=Some(i.value), username=i.username);
      added <- GroupDAO.addMember(g.itself, u.itself)
    ) yield g
  }


  /**
   * Creates users and groups from a CSV
   *
   * group name, parent group name, student number, last name, first name, preferred name, email address
   */
  def importFromCsv(a:Approval[User], rSet:RefWithId[GroupSet], csv:String):RefMany[Group] = {
    import au.com.bytecode.opencsv.CSVReader
    import java.io.StringReader

    import scala.collection.JavaConverters._

    val reader = new CSVReader(new StringReader(csv.trim()))
    val lines = reader.readAll().asScala.toSeq
    reader.close()

    val byGroup = lines.drop(1).groupBy(_(0))

    val groupCache = scala.collection.mutable.Map.empty[String, Ref[Group]]
    val userCache = scala.collection.mutable.Map.empty[String, Ref[User]]

    for {
      set <- rSet orIfNone UserError("We do need a group set for this")
      course <- set.course orIfNone UserError("We do need a course for this")

      (gname, glines) <- byGroup.toRefMany
      userIds <- {
        for {
          line <- glines.toRefMany

          Array(groupName, parent, studentNumber, surname, givenName, prefName, email) = line

          /*
           * Get the student. They may exist in the database, if not we need to create them.
           * Also memoize the result of either our creation or our search in case of duplicate rows in the CSV
           */
          user <- userCache.getOrElseUpdate(studentNumber.trim, {
            UserDAO.byIdentity(I_STUDENT_NUMBER, studentNumber) orIfNone {
              val user = UserDAO.unsaved.copy(
                nickname = Option(prefName).filter(_.trim.nonEmpty) orElse Option(givenName).filter(_.trim.nonEmpty),
                pwlogin = PasswordLogin(email=Option(email).filter(_.trim.nonEmpty)),
                identities = Seq(Identity(service=course.id.id, value=studentNumber.trim))
              )
              RegistrationDAO.register(user.id, course.id, Set(CourseRole.student))
              UserDAO.saveNew(user)
            }
          })
        } yield user.id
      }.toIds

      // Find the parent group if there is one
      parent <- optionally {
        val parentName = glines(0)(1)
        if (parentName.trim.isEmpty) {
          RefNone
        } else {
          groupCache.getOrElseUpdate(parentName, GroupDAO.byCourseAndName(course.itself, parentName))
        }
      }

      // Create the group
      group = GroupDAO.unsaved.copy(
        name = Some(gname),
        course = course.itself,
        set = set.itself,
        parent = Ref(parent),
        members = RefManyById(userIds.ids).of[User],
        provenance = Some("csv")
      )
      saved <- GroupDAO.saveNew(group)
    } yield saved
  }

  /**
   * Creates a group pre-enrolment from submitted CSV data
   * groupName,service,id,username
   */
  def gpreenrolFromCsv(course: RefWithId[Course], set:RefWithId[GroupSet], csv:String):Ref[GPreenrol] = {
    import au.com.bytecode.opencsv.CSVReader
    import java.io.StringReader

    import scala.collection.JavaConverters._

    val reader = new CSVReader(new StringReader(csv.trim()))
    val lines = reader.readAll().asScala.toSeq
    reader.close()

    val rGroupData = try {

      import GPreenrol._

      val names = lines.map(_(0)).toSet
      for (
        name <- names.toRefMany;
        group = GroupDAO.unsaved.copy(name=Some(name), course=course, set=set, provenance=Some("pre-enrol"));
        saved <- GroupDAO.saveNew(group);
        gd = GroupData(
          group = saved.itself,
          lookups = for (line <- lines.filter(_(0) == name)) yield {
            IdentityLookup(service=line(1), value=Option(line(2)).filter(_.trim.nonEmpty), username=Option(line(3)).filter(_.trim.nonEmpty))
          }
        )
      ) yield gd
    } catch {
      case ex:Throwable => RefFailed(ex)
    }

    for (gd <- rGroupData.toRefOne) yield GPreenrolDAO.unsaved.copy(course=course, set=set, groupData=gd.toSeq)
  }


}