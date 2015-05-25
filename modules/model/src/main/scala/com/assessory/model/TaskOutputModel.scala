package com.assessory.model

import java.io.StringWriter

import au.com.bytecode.opencsv.CSVWriter
import com.assessory.api._
import com.assessory.api.client.WithPerms
import com.assessory.api.critique.{Critique, CritiqueTask}
import com.assessory.asyncmongo._
import com.assessory.api.wiring.Lookups._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.Id._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.{BooleanQuestion, ShortTextQuestion, UserError, User}

object TaskOutputModel {

  def withPerms(a:Approval[User], t:TaskOutput) = {
    for {
      edit <- a.askBoolean(Permissions.EditOutput(t.itself))
    } yield {
      WithPerms(
        Map(
          "edit" -> edit
        ),
        t)
    }
  }

  def get(a:Approval[User], t:Id[TaskOutput,String]) = {
    for {
      to <- t.lazily
      wp <- withPerms(a, to)
    } yield wp
  }


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
      wp <- withPerms(a, finalised)
    } yield wp
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
      wp <- withPerms(a, finalised)
    } yield wp
  }

  def targetAsCsvString(a:Approval[User], t:Target):Ref[Seq[String]] = {
    t match {
      case TargetUser(id) =>
        for {
          u <- a.cache.lookUp(id)
          id <- u.identities.find(_.service == I_STUDENT_NUMBER).flatMap(_.value)
        } yield Seq(id, u.name.getOrElse(""))
      case TargetGroup(id) =>
        for {
          g <- a.cache.lookUp(id)
        } yield Seq(g.name.getOrElse(""))
      case TargetTaskOutput(id) =>
        for {
          to <- a.cache.lookUp(id)
          by <- targetAsCsvString(a, to.by)
        } yield by
      case _ => RefFailed(UserError("Can't represent this target as a string"))
    }
  }


  /**
   * Produces a CSV file of all the outputs for this task
   * @param a
   * @param t
   * @return
   */
  def asCsv(a:Approval[User], t:Id[Task,String]) = {
    val sWriter = new StringWriter()
    val writer = new CSVWriter(sWriter)

    val rTask = t.lazily

    def outputs = for {
      task <- rTask
      approved <- a ask Permissions.EditTask(task.itself)
      output <- TaskOutputDAO.byTask(task.itself)
    } yield output


    // We don't write a header because we don't know how many columns the "for" or "by" lines should take up.

    def write = (for {
      output <- outputs
      by <- targetAsCsvString(a, output.by)
      line <- output.body match {
        case c: Critique => for (ofor <- targetAsCsvString(a, c.target)) yield {
          by ++ ofor ++ (for (answer <- c.answers) yield answer.answer.map(_.toString).getOrElse(""))
        }
        case _ => RefFailed(UserError(s"I don't know how to make a CSV for ${output.body.kind}"))
      }
    } yield {
      writer.writeNext(line.toArray)
      true
    }).collect

    for (written <- write) yield {
      writer.close()
      sWriter.toString
    }
  }

}
