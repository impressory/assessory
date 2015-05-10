package com.assessory.model

import com.assessory.api._
import com.assessory.api.client.WithPerms
import com.assessory.api.wiring.Lookups
import Lookups._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.Id._
import com.wbillingsley.handy._
import com.assessory.asyncmongo._
import com.wbillingsley.handy.appbase.{GroupSet, User, Course}

object TaskModel {


  def withPerms(a:Approval[User], t:Task) = {
    for {
      edit <- a.askBoolean(Permissions.EditTask(t.itself))
      view <- a.askBoolean(Permissions.ViewTask(t.itself))
    } yield {
      WithPerms(
        Map(
          "edit" -> edit,
          "view" -> view
        ),
        t)
    }
  }


  def create(a:Approval[User], clientTask:Task) = {
    for {
      approved <- a ask Permissions.EditCourse(clientTask.course.lazily)
      unsaved = clientTask.copy(
        id = TaskDAO.allocateId.asId
      )
      saved <- TaskDAO.saveSafe(unsaved)
      wp <- withPerms(a, saved)
    } yield wp
  }

  def updateBody(a:Approval[User], clientTask:Task) = {
    for (
      approved <- a ask Permissions.EditTask(clientTask.id.lazily);
      saved <- TaskDAO.updateBody(clientTask);
      wp <- withPerms(a, saved)
    ) yield wp
  }

  def courseTasks(a:Approval[User], rCourse:Ref[Course]) = {
    for (
      c <- rCourse;
      approved <- a ask Permissions.ViewCourse(c.itself);
      t <- TaskDAO.byCourse(c.itself);
      wp <- withPerms(a, t)
    ) yield wp
  }

  def byId(a:Approval[User], t:Id[Task,String]) = {
    for (
      task <- t.lazily;
      wp <- withPerms(a, task); // Do this first so we cache the permissions
      approved <- a ask Permissions.ViewTask(task.itself)
    ) yield wp
  }

}
