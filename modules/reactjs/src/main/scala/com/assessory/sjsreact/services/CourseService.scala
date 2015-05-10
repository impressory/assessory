package com.assessory.sjsreact.services

import com.assessory.api.client.WithPerms
import com.assessory.clientpickle.Pickles._
import com.assessory.sjsreact.Latched
import com.wbillingsley.handy._
import Id._
import com.wbillingsley.handy.appbase.{User, Course}
import org.scalajs.dom.ext.Ajax

import scala.collection.mutable
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow


object CourseService {

  val cache = mutable.Map.empty[String, Latched[WithPerms[Course]]]

  val myCourses = Latched.future(
    Ajax.post("/api/course/my", headers = Map("Accept" -> "application/json")).responseText.map(upickle.read[Seq[Course]]).optional404
  )
  UserService.self.listeners.add { case _ => myCourses.clear() }

  def loadId[KK <: String](id:Id[Course,KK]) = {
    Ajax.get(s"/api/course/${id.id}", headers = Map("Accept" -> "application/json")).responseText.map(upickle.read[WithPerms[Course]])
  }

  def latch(s:String):Latched[WithPerms[Course]] = cache.getOrElseUpdate(s, Latched.future(loadId(s.asId[Course])))

  def latch(id:Id[Course,String]):Latched[WithPerms[Course]] = cache.getOrElseUpdate(id.id, Latched.future(loadId(id)))
}
