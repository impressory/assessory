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
import com.assessory.api.critique._
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.JsValue

import com.assessory.api.wiring.Lookups._

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
      task <- a.cache(rTask);
      approved <- a ask Permissions.EditCourse(task.course);
      h <- task.body match {
        case Some(gct:CritiqueTask) => {
          val qs =  gct.questionnaire.questions.map { q => "\"" + q.prompt.replace("\"", "\"\"") + "\"," }
          (qs.fold("student number, nickname, target, ")(_ + _) + "\n").itself
        }
        case _ => RefFailed(new IllegalStateException("Unknown task body type"))
      }
    ) yield h

    val body = for {
      h <- header;
      task <- a.cache(rTask)
      output <- TaskOutputDAO.byTask(rTask);
      line <- output.body match {
        case Some(gc: Critique) => {
          for {
            user <- a.cache(output.byUser);
            sIdentity <- user.getIdentity(I_STUDENT_NUMBER).toRef
            sId = sIdentity.value
            userName = user.nickname.getOrElse("Anonymous");
            targetName <- gc.target match {
              case CTGroup(rg) => (for {
                g <- a.cache(rg)
                n <- g.name
              } yield n) orIfNone "Unnamed group".itself
              case CTTaskOutput(rto) => (for {
                to <- a.cache(rto)
                u <- a.cache(to.byUser)
                sIdentity <- u.getIdentity(I_STUDENT_NUMBER).toRef
                v <- sIdentity.value
              } yield v) orIfNone "Unnamed user".itself
            }
          } yield {
            def cell(s:String):String = "\"" + s.replace("\"", "\"\"") + "\","
            def cellO(o:Option[String]):String = o match {
              case Some(s) => cell(s)
              case _ => ""
            }

            val as = gc.answers.map {
              a => cell(a.answerAsString)
            }
            as.fold(cellO(sId) concat cell(userName) concat cell(targetName))(_ + _) + "\n"
          }
        }
        case _ => RefFailed(new IllegalStateException("Unknown task output body type"))
      }
    } yield line

    val enum = for (h <- header) yield {
      import com.wbillingsley.handyplay.RefConversions._
      import play.api.libs.concurrent.Execution.Implicits.defaultContext
      Enumerator(h) andThen body.enumerate
    }
    enum
  }

}