package com.assessory.model

import com.assessory.api._
import com.assessory.asyncmongo._
import com.assessory.api.wiring.Lookups._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.Id._
import com.wbillingsley.handy._
import com.wbillingsley.handy.user.User

object TaskOutputModel {

  def myOutputs(a:Approval[User], rTask:Ref[Task]) = {
    for {
      task <- rTask
      uid <- a.who.refId
      to <- TaskOutputDAO.byTaskAndBy(task.id, TargetUser(uid))
    } yield to
  }

  def create(a:Approval[User], task:Ref[Task], clientTaskOutput:TaskOutput, finalise:Boolean) = {
    for {
      u <- a.who
      t <- task
      approved <- a ask Permissions.ViewCourse(t.course.lazily)
      to = clientTaskOutput.copy(
        id=TaskOutputDAO.allocateId.asId,
        task=t.id,
        by=TargetUser(u.id)
      )
      saved <- TaskOutputDAO.saveSafe(to)
      finalised <- if (finalise) {
        // Finalise the task output
        TaskOutputDAO.finalise(saved)
      } else {
        // Don't finalise it; just return the saved item
        saved.itself
      }
    } yield finalised
  }

  def updateBody(a:Approval[User], clientTaskOutput:TaskOutput, finalise:Boolean) = {
    for {
      approved <- a ask Permissions.EditOutput(clientTaskOutput.id.lazily)
      saved <- TaskOutputDAO.updateBody(clientTaskOutput)
      finalised <- if (finalise) {
        // Finalise the task output
        TaskOutputDAO.finalise(saved)
      } else {
        // Don't finalise it; just return the saved item
        saved.itself
      }
    } yield finalised
  }

  /*
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
      Enumerator(h) andThen body.enumerate
    }
    enum
  }
  */

}
