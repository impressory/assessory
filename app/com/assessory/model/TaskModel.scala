package com.assessory.model

import com.assessory.reactivemongo._
import com.assessory.play.json._

import com.assessory.api._
import course._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import play.api.libs.json.JsValue

object TaskModel {


  def create(a:Approval[User], rCourse:Ref[Course], json:JsValue) = {
    for (
      c <- rCourse;
      approved <- a ask Permissions.EditCourse(c.itself);
      t = TaskToJson.update(TaskDAO.unsaved.copy(course=c.itself), json);
      saved <- TaskDAO.saveNew(t)
    ) yield saved
  }

  def updateBody(a:Approval[User], rTask:Ref[Task], json:JsValue) = {
    for (
      t <- rTask;
      a <- a ask Permissions.EditTask(t.itself);
      updated = TaskToJson.update(t, json);
      saved <- TaskDAO.updateBody(updated)
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