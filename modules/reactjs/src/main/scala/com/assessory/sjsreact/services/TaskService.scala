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

  UserService.self.listeners.add { case _ => cache.clear() }

  def courseTasks(courseId:Id[Course,String]) = Latched.lazily(
    Ajax.get(s"/api/course/${courseId.id}/tasks", headers = Map("Accept" -> "application/json")).responseText.map(upickle.read[Seq[WithPerms[Task]]])
  )

  def loadId[KK <: String](id:Id[Task,KK]) = {
    val t = Ajax.get(s"/api/task/${id.id}", headers = Map("Accept" -> "application/json")).responseText
    //t.onComplete { text => println(text); 1 }
    t.map(upickle.read[WithPerms[Task]])
  }

  def latch(s:String):Latched[WithPerms[Task]] = latch(s.asId)

  def latch(id:Id[Task,String]):Latched[WithPerms[Task]] = cache.getOrElseUpdate(id.id, Latched.lazily(loadId(id)))

}
