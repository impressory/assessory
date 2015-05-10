package com.assessory.sjsreact.services

import com.assessory.api.Task
import com.assessory.api.client.WithPerms
import com.assessory.clientpickle.Pickles._
import com.assessory.sjsreact.Latched
import com.wbillingsley.handy.Id
import Id._
import com.wbillingsley.handy.appbase.{Group, Course}
import org.scalajs.dom.ext.Ajax

import scala.collection.mutable
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

object TaskService {

  val cache = mutable.Map.empty[String, Latched[WithPerms[Task]]]

  def courseTasks(courseId:Id[Course,String]) = Latched.future(
    Ajax.get(s"/api/course/${courseId.id}/tasks", headers = Map("Accept" -> "application/json")).responseText.map(upickle.read[Seq[WithPerms[Task]]])
  )

  def loadId[KK <: String](id:Id[Task,KK]) = {
    Ajax.get(s"/api/task/${id.id}", headers = Map("Accept" -> "application/json")).responseText.map(upickle.read[WithPerms[Task]])
  }

  def latch(s:String) = cache.getOrElseUpdate(s, Latched.future(loadId(s.asId[Task])))

}
