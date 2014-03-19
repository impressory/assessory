package com.assessory.model


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
import com.assessory.api.groupcrit._
import play.api.libs.iteratee.Enumerator
import com.assessory.api.outputcrit._
import play.api.libs.json.JsValue


object TaskOutputModel {


  def relevantToMe(a:Approval[User], rTask:Ref[Task]) = {
    for (
      task <- rTask;
      to <- TaskOutputDAO.relevantTo(task, a.who)
    ) yield to
  }

  def myOutputs(a:Approval[User], rTask:Ref[Task]) = {
    for (
      task <- rTask;
      to <- TaskOutputDAO.byTaskAndUser(task.itself, a.who)
    ) yield to
  }

  def create(a:Approval[User], rTask: Ref[Task], json:JsValue) = {
    for (
      u <- a.who;
      task <- rTask;
      to = TaskOutputDAO.unsaved.copy(byUser=u.itself, task=task.itself);
      approved <- a ask Permissions.ViewCourse(task.course);
      saved <- {
        val updated = TaskOutputToJson.update(to, json);
        TaskOutputDAO.saveNew(updated)
      };
      finalised <- if ((json \ "finalise").asOpt[Boolean].getOrElse(false)) {
        // Finalise the task output
        TaskOutputDAO.finalise(saved)
      } else {
        // Don't finalise it; just return the saved item
        saved.itself
      }
    ) yield finalised
  }

  def updateBody(a:Approval[User], rTaskOutput:Ref[TaskOutput], json:JsValue):Ref[JsValue] = {
    for (
      output <- rTaskOutput;
      approved <- a ask Permissions.EditOutput(output.itself);
      updated = TaskOutputToJson.update(output, json);
      saved <- TaskOutputDAO.updateBody(updated);
      finalised <- if ((json \ "finalise").asOpt[Boolean].getOrElse(false)) {
        // Finalise the task output
        TaskOutputDAO.finalise(saved)
      } else {
        // Don't finalise it; just return the saved item
        saved.itself
      };
      j <-TaskOutputToJson.toJsonFor(finalised, new Approval(a.who))
    ) yield j
  }

  def asCsv(a:Approval[User], rTask:Ref[Task]) = {
    val header = for (
      task <- rTask;
      approved <- a ask Permissions.EditCourse(task.course);
      h <- task.body match {
        case Some(gct:GroupCritTask) => {
          val qs =  gct.questionnaire.questions.map { q => "\"" + q.prompt.replace("\"", "\"\"") + "\"," }
          (qs.fold("student, group, ")(_ + _) + "\n").itself
        }
        case Some(oct:OutputCritTask) => {
          val qs =  oct.questionnaire.questions.map { q => "\"" + q.prompt.replace("\"", "\"\"") + "\"," }
          (qs.fold("student, crit by, crit for group,")(_ + _) + "\n").itself
        }
        case _ => RefFailed(new IllegalStateException("Unknown task body type"))
      }
    ) yield h

    val body = for (
      h <- header;
      output <- TaskOutputDAO.byTask(rTask);
      line <- output.body match {
        case Some(gc:GCritique) => {
          for (
            user <- a.cache(output.byUser);
            userName = user.nickname.getOrElse("Anonymous");
            group <- a.cache(gc.forGroup);
            groupName = group.name.getOrElse("Unnamed group")
          ) yield {
            val as = gc.answers.map { a => "\"" + a.answerAsString.replace("\"", "\"\"") + "\"," }
            val unr = userName.replace("\"","\"\"")
            val gnr = groupName.replace("\"","\"\"")
            as.fold("\"" + unr + "\",\"" + gnr + "\",")(_ + _) + "\n"
          }
        }
        case Some(oct:OCritique) => {
          for (
            user <- a.cache(output.byUser);
            userName = user.nickname.getOrElse("Anonymous");
            output <- a.cache(oct.forOutput);
            otherUser <- a.cache(output.byUser);
            otherUserName = otherUser.nickname.getOrElse("Anonymous");
            gc <- output.body match {
              case Some(gc:GCritique) => gc.itself
              case _ => RefFailed(new IllegalStateException("Wasn't critiquing a group crit"))
            };
            group <- a.cache(gc.forGroup);
            groupName = group.name.getOrElse("Unnamed group")
          ) yield {
            val as = oct.answers.map { a => "\"" + a.answerAsString.replace("\"", "\"\"") + "\"," }
            val unr = userName.replace("\"","\"\"")
            val ounr = otherUserName.replace("\"","\"\"")
            val gnr = groupName.replace("\"","\"\"")
            as.fold("\"" + unr + "\",\"" + ounr + "\",\"" + gnr + "\",")(_ + _) + "\n"
          }
        }
        case _ => RefFailed(new IllegalStateException("Unknown task output body type"))
      }
    ) yield line

    val enum = for (h <- header) yield {
      import com.wbillingsley.handyplay.RefConversions._
      import play.api.libs.concurrent.Execution.Implicits.defaultContext
      Enumerator(h) andThen body.enumerate
    }
    enum
  }

}