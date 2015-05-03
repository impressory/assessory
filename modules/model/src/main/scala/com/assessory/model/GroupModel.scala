package com.assessory.model

import java.io.StringReader

import au.com.bytecode.opencsv.CSVReader
import com.assessory.api._
import com.assessory.api.client.WithPerms
import com.assessory.asyncmongo._
import com.assessory.api.wiring.Lookups._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.Id._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase._

import scala.collection.JavaConverters._

object GroupModel {

  def withPerms(a:Approval[User], gs:GroupSet) = {
    for {
      edit <- a.askBoolean(Permissions.EditGroupSet(gs.itself))
      view <- a.askBoolean(Permissions.ViewGroupSet(gs.itself))
    } yield {
      WithPerms(
        Map(
          "edit" -> edit,
          "view" -> view
        ),
        gs)
    }
  }

  def createGroupSet(a:Approval[User], clientGS:GroupSet) = {
    for {
      approved <- a ask Permissions.EditCourse(clientGS.course.lazily)
      unsaved = clientGS.copy(id=GroupSetDAO.allocateId.asId)
      saved <- GroupSetDAO.saveNew(unsaved)

      wp <- withPerms(a, saved)
    } yield wp
  }

  def editGroupSet(a:Approval[User], clientGS:GroupSet) = {
    for {
      approved <- a ask Permissions.EditGroupSet(clientGS.id.lazily)
      saved <- GroupSetDAO.saveDetails(clientGS)
      wp <- withPerms(a, saved)
    } yield wp
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
   * The groups belonging to a particular group set
   */
  def groupSetGroups(a:Approval[User], rGS:Ref[GroupSet]) = {
    for {
      gs <- rGS
      approved <- a ask Permissions.ViewGroupSet(gs.itself)
      g <- GroupDAO.bySet(gs.id)
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
  def doPreenrolments(course:RefWithId[Course], user:Ref[User]):RefMany[Group.Reg]= {
    for {
      u <- user
      gs <- GroupSetDAO.byCourse(course)
      reg <- doPreenrolments(u, gs.id)
    } yield reg
  }

  /**
   * Searches for course pre-enrolments, and performs them
   */
  def doPreenrolments(user:User, gsId:Id[GroupSet, String]) = {
    for {
      i <- user.identities.toRefMany
      p <- PreenrolmentDAO.group.withinByIdentity(within=gsId,service=i.service, value=i.value, username=i.username)
      (row, idx) <- p.rows.zipWithIndex.filter{ case (row, idx) => i.matches(row.identity.service, row.identity.value, row.identity.username) }.toRefMany
      reg <- RegistrationDAO.group.register(user.id, row.target, row.roles, EmptyKind)
      used <- PreenrolmentDAO.group.useRow(p.id, idx, reg.id)
    } yield reg
  }


  private def _importFromCsv(set:GroupSet, roles:Set[GroupRole], csv:String):RefMany[Group.Reg] = {
    val reader = new CSVReader(new StringReader(csv.trim()))
    val lines = reader.readAll().asScala.toSeq.drop(1)
    reader.close()

    def opt(s:String) = Option(s).filter(_.trim.nonEmpty)

    // Get the student names
    val studentNumNames = lines.map(l => l(0) -> l(1)).toMap

    val rStudentMap = ensureUsers(set.course, studentNumNames)

    set.parent match {
      case Some(parentGsId) =>
        // Get the group names
        val parentGroupNames = (for {
          l <- lines
          n <- opt(l(3))
        } yield n).toSet

        val groupedByParent = lines.groupBy(_(3))
        for {
          studentMap <- rStudentMap

          // Create the parent groups if needed
          parentMap <- ensureGroups(parentGsId, parentGroupNames, None)
          (p, childLines) <- groupedByParent.toRefMany

          // Get the group names
          groupNames = (for {
            l <- childLines
            n <- opt(l(2))
          } yield n).toSet

          groupMap <- ensureGroups(set.id, groupNames, Some(parentMap(p).id))
          l <- lines.toRefMany
          u = studentMap(l(0))
          g = groupMap(l(1))
          reg <- RegistrationDAO.group.register(u.id, g.id,roles, EmptyKind)
        } yield reg

      case _ => for {
        studentMap <- rStudentMap

        // Get the group names
        groupNames = (for {
          l <- lines
          n <- opt(l(2))
        } yield n).toSet

        groupMap <- ensureGroups(set.id, groupNames, None)
        l <- lines.toRefMany
        u = studentMap(l(0))
        g = groupMap(l(1))
        reg <- RegistrationDAO.group.register(u.id, g.id,roles, EmptyKind)
      } yield reg
    }
  }

  /**
   * Creates users and groups from a CSV
   *
   * student number, name, group name, parent
   */
  def importFromCsv(a:Approval[User], setId:Id[GroupSet,String], csv:String):RefMany[Group.Reg] = {
    for {
      set <- setId.lazily orIfNone UserError("We do need a group set for this")
      approved <- a.ask(Permissions.EditCourse(set.course.lazily))

      reg <- _importFromCsv(set, Set(GroupRole.member), csv)
    } yield reg
  }

  // Map names to groups, making new groups as necessary
  private def ensureGroups(gsId:Id[GroupSet, String], names:Set[String], parent:Option[Id[Group,String]]) = for {
  // Get the existing groups' names, and find which are missing
    existing <- GroupDAO.byNames(gsId, names).collect
    existingNames = for (g <- existing; n <- g.name) yield n
    missing = names.diff(existingNames.toSet)

    // Create groups for the missing names
    created <- (for {
      name <- missing.toRefMany
      unsaved = new Group(id=GroupDAO.allocateId.asId, set=gsId, parent=parent, name=Some(name))
      saved <- GroupDAO.saveNew(unsaved)
    } yield saved).collect

    all = existing ++ created
  } yield all.map(g => g.name.get -> g).toMap

  // Map names to groups, making new groups as necessary
  private def ensureUsers(cId:Id[Course, String], studentNumNames:Map[String, String]) = {

    def sNum(u:User) = for {
      i <- u.identities.find(_.service == I_STUDENT_NUMBER)
      v <- i.value
    } yield v

    for {
      // Get the existing groups' names, and find which are missing
      existing <- (for {
        (num, name) <- studentNumNames.toRefMany
        u <- UserDAO.bySocialIdOrUsername(I_STUDENT_NUMBER, Some(num), Some(num))
      } yield u).collect

      existingNums = for {
        u <- existing
        v <- sNum(u)
      } yield v

      missing = studentNumNames.keySet.diff(existingNums.toSet)

      // Create students for the missing numbers
      created <- (for {
        num <- missing.toRefMany
        unsaved = new User(
          id=UserDAO.allocateId.asId,
          name=studentNumNames.get(num),
          nickname=studentNumNames.get(num),
          identities=Seq(Identity(service=I_STUDENT_NUMBER, value=Some(num), username=Some(num)))
        )
        saved <- UserDAO.saveNew(unsaved)

        reg <- RegistrationDAO.course.register(saved.id, cId, Set(CourseRole.student), EmptyKind)
      } yield saved).collect

      all = existing ++ created
    } yield all.map(u => sNum(u).get -> u).toMap
  }


  /**
   * Creates a group preenrolment
   * @param a
   * @param name
   * @param gsId
   * @param roles
   * @param csv
   * @return
   */
  def createPreenrol(a:Approval[User], name:String, gsId:Id[GroupSet, String], roles:Set[GroupRole], csv:String) = {
    val reader = new CSVReader(new StringReader(csv.trim()))
    val lines = reader.readAll().asScala.toSeq.drop(1)
    reader.close()

    def opt(s:String) = Option(s).filter(_.trim.nonEmpty)

    // Get the group names
    val names = lines.map(_(0)).toSet

    for {
      gs <- gsId.lazily
      approved <- a ask Permissions.EditCourse(gs.course.lazily)

      // Map names to groups, making new groups as necessary
      // TODO: Deal with parent groups in pre-enrol
      groupMap <- ensureGroups(gsId, names, None)

      unsaved = new Group.Preenrol(
        id = PreenrolmentDAO.course.allocateId.asId,
        name = Some(name),
        rows = for {
          l <- lines
          g = groupMap(l(2))
          il = IdentityLookup(l(0), opt(l(1)), opt(l(1)))
        } yield new Group.PreenrolRow(roles, g.id, il)
      )
      saved <- PreenrolmentDAO.group.saveSafe(unsaved)
    } yield saved
  }

}