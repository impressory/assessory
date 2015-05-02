package com.assessory.model

import com.assessory.api._
import com.assessory.api.wiring.Lookups
import Lookups._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.Id._
import com.wbillingsley.handy._
import com.assessory.asyncmongo._
import com.wbillingsley.handy.appbase.Course
import com.wbillingsley.handy.user.User

object TaskModel {


  def create(a:Approval[User], clientTask:Task) = {
    for {
      approved <- a ask Permissions.EditCourse(clientTask.course.lazily)
      unsaved = clientTask.copy(
        id = TaskDAO.allocateId.asId
      )
      saved <- TaskDAO.saveSafe(unsaved)
    } yield saved
  }

  def updateBody(a:Approval[User], clientTask:Task) = {
    for (
      a <- a ask Permissions.EditTask(clientTask.id.lazily);
      saved <- TaskDAO.updateBody(clientTask)
    ) yield saved
  }

  def courseTasks(a:Approval[User], rCourse:Ref[Course]) = {
    for (
      c <- rCourse;
      approved <- a ask Permissions.ViewCourse(c.itself);
      t <- TaskDAO.byCourse(c.itself)
    ) yield t
  }

}
