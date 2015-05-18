package com.assessory.sjsreact.services

import com.assessory.api.critique.CritAllocation
import com.assessory.api.{Target, TaskOutput, Task}
import com.assessory.api.client.WithPerms
import com.assessory.clientpickle.Pickles._
import com.assessory.sjsreact.{WebApp, Latched}
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.Id._
import com.wbillingsley.handy.appbase.Course
import org.scalajs.dom.ext.Ajax

import scala.collection.mutable
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

object TaskOutputService {

  val cache = mutable.Map.empty[String, Future[WithPerms[TaskOutput]]]

  UserService.self.listeners.add { case _ => cache.clear() }

  def courseTasks(courseId:Id[Course,String]) = Latched.lazily(
    Ajax.get(s"/api/course/${courseId.id}/tasks", headers=AJAX_HEADERS).responseText.map(upickle.read[Seq[WithPerms[Task]]])
  )

  def myAllocations(taskId:Id[Task,String]) = {
    Ajax.get(s"/api/critique/${taskId.id}/myAllocations", headers=AJAX_HEADERS).responseText.map(upickle.read[Seq[Target]])
  }

  def findOrCreateCrit(taskId:Id[Task,String], target:Target) = {
    val fwp = Ajax.post(s"/api/critique/${taskId.id}/findOrCreateCrit", upickle.write(target), headers=AJAX_HEADERS).responseText.map(upickle.read[WithPerms[TaskOutput]])
    fwp.onComplete(_ => WebApp.rerender())
    for { wp <- fwp } yield {
      cache.put(wp.item.id.id, fwp)
      wp.item.id
    }
  }

  def updateBody(to:TaskOutput) = {
    val fto = Ajax.post(s"/api/taskoutput/${to.id.id}", upickle.write(to), headers=AJAX_HEADERS).responseText.map(upickle.read[WithPerms[TaskOutput]])
    cache.put(to.id.id, fto)
    fto
  }


  def loadId[KK <: String](id:Id[TaskOutput,KK]) = {
    val fwp = Ajax.get(s"/api/task/${id.id}", headers=AJAX_HEADERS).responseText.map(upickle.read[WithPerms[TaskOutput]])
    fwp.onComplete(_ => WebApp.rerender())
    fwp
  }

  def latch(s:String):Latched[WithPerms[TaskOutput]] = latch(s.asId)

  def latch(id:Id[TaskOutput,String]):Latched[WithPerms[TaskOutput]] = Latched.lazily(cache.getOrElseUpdate(id.id, loadId(id)))

  def future(id:Id[TaskOutput,String]) = cache.getOrElseUpdate(id.id, loadId(id))

}
