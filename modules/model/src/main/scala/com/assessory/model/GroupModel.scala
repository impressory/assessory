package com.assessory.model

import java.io.StringReader

import au.com.bytecode.opencsv.CSVReader
import com.assessory.api._
import com.assessory.api.client.WithPerms
import com.assessory.asyncmongo._
import com.assessory.api.wiring.Lookups._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.Id._
import com.wbillingsley.handy.Ids._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase._

import scala.collection.JavaConverters._

object GroupModel {

  def withPerms(a:Approval[User], gs:GroupSet):Ref[WithPerms[GroupSet]] = {
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

  def withPerms(a:Approval[User], g:Group):Ref[WithPerms[Group]] = {
    for {
      edit <- a.askBoolean(Permissions.EditGroup(g.itself))
      view <- a.askBoolean(Permissions.ViewGroup(g.itself))
    } yield {
      WithPerms(
        Map(
          "edit" -> edit,
          "view" -> view
        ),
        g)
    }
  }

  def groupSet(a:Approval[User], gsId:Id[GroupSet, String]) = {
    for {
      gs <- gsId.lazily
      wp <- withPerms(a, gs)
    } yield wp
  }

  def group(a:Approval[User], gsId:Id[Group, String]) = {
    for {
      gs <- gsId.lazily
      wp <- withPerms(a, gs)
    } yield wp
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


  def myGroupsInCourseWP(a:Approval[User], rCourse:Ref[Course]) = {
    for {
      g <- myGroupsInCourse(a, rCourse)
      wp <- withPerms(a, g)
    } yield wp
  }


  def myGroupsInCourse(a:Approval[User], rCourse:Ref[Course]) = {
    for {
      u <- a.who
      course <- rCourse
      groupRegs <- RegistrationDAO.group.byUser(u.id).collect
      groupIds = groupRegs.map(_.target.id).asIds[Group]
      group <- groupIds.lookUp if group.course == Some(course.id)
    } yield group
  }

  def myGroupsWP(a:Approval[User]) = {
    for {
      g <- myGroups(a)
      wp <- withPerms(a, g)
    } yield wp
  }

  def myGroups(a:Approval[User]) = {
    for {
      u <- a.who
      groupRegs <- RegistrationDAO.group.byUser(u.id).collect
      groupIds = groupRegs.map(_.target.id).asIds[Group]
      group <- groupIds.lookUp
    } yield group
  }

  def findMany(a:Approval[User], oIds:Ids[Group,String]) = {
    oIds.lookUp
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



  // student number, name, group name, parent, social login
  private def _importFromCsv(set:GroupSet, roles:Set[GroupRole], csv:String):RefMany[Group.Reg] = {
    val reader = new CSVReader(new StringReader(csv.trim()))
    val rawLines = reader.readAll().asScala.toSeq
    reader.close()

    if (rawLines.size == 0) {
      return UserError("CSV contained no lines, not even a header")
    }

    // Simple method to get an option from a CSV entry
    def opt(s:String) = Option(s).filter(_.trim.nonEmpty)

    val header = rawLines(0)
    val bodyLines = rawLines.drop(1)

    def studentNum(l:Array[String]) = opt(l(0))
    def name(l:Array[String]) = opt(l(1))
    def groupName(l:Array[String]) = opt(l(2))
    def parentGroupName(l:Array[String]) = opt(l(3))
    def socialId(l:Array[String]) = if (l.length > 4) opt(l(4)) else None

    // The service name of the social login
    val socialService = socialId(header)

    // Ensure there is a user for every line in the CSV
    val rUsers = for {
      line <- bodyLines.toRefMany

      identities = Seq(Identity(I_STUDENT_NUMBER, studentNum(line), studentNum(line))) ++ (for {
        service <- socialService
        id <- socialId(line)
      } yield Identity(service=service, username=Some(id), value=Some(id)))

      u <- ensureUser(
        User(
          id = "invalid".asId,
          name = name(line)
        ),
        identities = identities
      )

      reg <- RegistrationDAO.course.register(u.id, set.course, Set(CourseRole.student), EmptyKind)
    } yield u

    // Get a student number from a user
    def sNum(u:User) = for {
      i <- u.identities.find(_.service == I_STUDENT_NUMBER)
      v <- i.value
    } yield v

    val rStudentMap = for (users <- rUsers.collect) yield users.map(u => sNum(u) -> u).toMap

    set.parent match {
      case Some(parentGsId) =>
        // Get the group names
        val parentGroupNames = (for {
          l <- bodyLines
          n <- parentGroupName(l)
        } yield n).toSet

        val groupedByParent = bodyLines.groupBy(parentGroupName)
        for {
          parentGS <- parentGsId.lazily orIfNone UserError("Parent group set was not found")
          studentMap <- rStudentMap

          // Create the parent groups if needed
          parentMap <- ensureGroups(parentGS, parentGroupNames, None)
          (optP, childLines) <- groupedByParent.toRefMany
          p <- optP.toRef

          // Get the group names
          groupNames = (for {
            l <- childLines
            n <- groupName(l)
          } yield n).toSet

          groupMap <- ensureGroups(set, groupNames, Some(parentMap(p).id))
          l <- childLines.toRefMany
          u = studentMap(studentNum(l))
          gn <- groupName(l).toRef
          g = groupMap(gn)
          reg <- RegistrationDAO.group.register(u.id, parentMap(p).id,roles, EmptyKind)
          reg <- RegistrationDAO.group.register(u.id, g.id,roles, EmptyKind)
        } yield reg

      case _ => for {
        studentMap <- rStudentMap

        // Get the group names
        groupNames = (for {
          l <- bodyLines
          n <- groupName(l)
        } yield n).toSet

        groupMap <- ensureGroups(set, groupNames, None)
        l <- bodyLines.toRefMany
        gn <- groupName(l).toRef
        u = studentMap(studentNum(l))
        g = groupMap(gn)
        reg <- RegistrationDAO.group.register(u.id, g.id,roles, EmptyKind)
      } yield reg
    }
  }

  /**
   * Creates users and groups from a CSV
   *
   * student number, name, group name, parent, social identity
   */
  def importFromCsv(a:Approval[User], setId:Id[GroupSet,String], csv:String):RefMany[Group.Reg] = {
    for {
      set <- setId.lazily orIfNone UserError("We do need a group set for this")
      approved <- a.ask(Permissions.EditCourse(set.course.lazily))

      reg <- _importFromCsv(set, Set(GroupRole.member), csv)
    } yield reg
  }

  // Map names to groups, making new groups as necessary
  private def ensureGroups(gs:GroupSet, names:Set[String], parent:Option[Id[Group,String]]):Ref[Map[String,Group]] = for {
  // Get the existing groups' names, and find which are missing
    existing <- GroupDAO.byNames(gs.id, names).collect
    existingNames = for (g <- existing; n <- g.name) yield n
    missing = names.diff(existingNames.toSet)

    // Create groups for the missing names
    created <- (for {
      name <- missing.toRefMany
      unsaved = new Group(id=GroupDAO.allocateId.asId, set=gs.id, course=Some(gs.course), parent=parent, name=Some(name))
      saved <- GroupDAO.saveNew(unsaved)
    } yield saved).collect

    all = existing ++ created
  } yield all.map(g => g.name.get -> g).toMap


  /**
   * Ensures a user exists in the database with the specified identities
   */
  def ensureUser(template:User, identities:Seq[Identity]):Ref[User] = {
    println("Ensure user " + identities)

    // Fetch or create the user
    val found = (identities.toRefMany.fold[Ref[User]](RefNone) { case(ru, i) =>
      ru orIfNone { println("none"); UserDAO.bySocialIdOrUsername(i.service, i.username, i.value) }
    }).flatten

    val ensured = found orIfNone { println("creating"); UserDAO.saveNew(template.copy(id=UserDAO.allocateId.asId, identities = identities)) }

    // Add any missing identities
    val updates = for {
      u <- ensured
      missing = identities diff u.identities
      i <- missing.toRefMany.fold[Ref[User]](u.itself) { case (ref, i) => UserDAO.pushIdentity(ref, i) }
    } yield i

    updates.flatten
  }


  // Map names to groups, making new groups as necessary
  private def ensureUsrs(cId:Id[Course, String], studentNumNames:Map[String, String]) = {

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
   * Creates a group preenrolment from a CSV
   *
   * social-login-service, social-login-id-or-username, group name, parent group name
   */
  def createPreenrol(a:Approval[User], name:String, gsId:Id[GroupSet, String], roles:Set[GroupRole], csv:String) = {
    val reader = new CSVReader(new StringReader(csv.trim()))
    val lines = reader.readAll().asScala.toSeq.drop(1)
    reader.close()

    def opt(s:String) = Option(s).filter(_.trim.nonEmpty)


    for {
      gs <- gsId.lazily
      approved <- a ask Permissions.EditCourse(gs.course.lazily)

      p <- gs.parent match {
        // If the GroupSet has a parent GroupSet, we need to ensure the parent groups exist
        case Some(parentGsId) =>
          val groupedByParent = lines.groupBy(_(3).trim)
          for {
            parentGs <- parentGsId.lazily
            parentMap <- ensureGroups(parentGs, groupedByParent.keySet, None)
            groupMap <- (for {
              (parentName, groupLines) <- groupedByParent.toRefMany
              groupNames = groupLines.map(_(2).trim).toSet
              gm <- ensureGroups(gs, groupNames, parentMap.get(parentName).map(_.id))
            } yield gm).fold[Map[String,Group]](Map.empty)(_ ++ _)

            unsaved = new Group.Preenrol(
              id = PreenrolmentDAO.course.allocateId.asId,
              name = Some(name),
              rows = for {
                l <- lines
                g = groupMap(l(2).trim)
                il = IdentityLookup(l(0), opt(l(1)), opt(l(1)))
              } yield new Group.PreenrolRow(roles, g.id, il)
            )

            saved <- PreenrolmentDAO.group.saveSafe(unsaved)
          } yield saved

        case None =>
          for {
            groupMap <- ensureGroups(gs, lines.map(_(2).trim).toSet, None)

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
    } yield p
  }

}
