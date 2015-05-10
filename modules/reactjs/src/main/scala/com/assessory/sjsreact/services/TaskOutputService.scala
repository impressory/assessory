package com.assessory.sjsreact.services

import com.assessory.api.critique.CritAllocation
import com.assessory.api.{Target, TaskOutput, Task}
import com.assessory.api.client.WithPerms
import com.assessory.clientpickle.Pickles._
import com.assessory.sjsreact.Latched
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.Id._
import com.wbillingsley.handy.appbase.Course
import org.scalajs.dom.ext.Ajax

import scala.collection.mutable
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

object TaskOutputService {

  val cache = mutable.Map.empty[String, Latched[WithPerms[TaskOutput]]]

  def courseTasks(courseId:Id[Course,String]) = Latched.future(
    Ajax.get(s"/api/course/${courseId.id}/tasks", headers = Map("Accept" -> "application/json")).responseText.map(upickle.read[Seq[WithPerms[Task]]])
  )

  def myAllocations(taskId:Id[Task,String]) = Latched.future({
    Ajax.get(s"/api/critique/${taskId.id}/myAllocations", headers = Map("Accept" -> "application/json")).responseText.map(upickle.read[Seq[Target]])
  })


  def loadId[KK <: String](id:Id[TaskOutput,KK]) = {
    Ajax.get(s"/api/task/${id.id}", headers = Map("Accept" -> "application/json")).responseText.map(upickle.read[WithPerms[TaskOutput]])
  }


  def latch(s:String) = cache.getOrElseUpdate(s, Latched.future(loadId(s.asId[TaskOutput])))

}
