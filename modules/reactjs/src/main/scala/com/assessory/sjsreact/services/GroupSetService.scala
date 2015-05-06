package com.assessory.sjsreact.services

import com.assessory.api.client.WithPerms
import com.assessory.clientpickle.Pickles._
import com.assessory.sjsreact.Latched
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.Id._
import com.wbillingsley.handy.appbase.{GroupSet, Course, Group}
import org.scalajs.dom.ext.Ajax

import scala.collection.mutable
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

object GroupSetService {

  val cache = mutable.Map.empty[String, Latched[WithPerms[GroupSet]]]

  def myGroups(courseId:Id[Course,String]) = Latched.future(
    Ajax.post(s"/api/course/${courseId.id}/group/my", headers = Map("Accept" -> "application/json")).responseText.map(upickle.read[Seq[GroupSet]])
  )

  def loadId[KK <: String](id:Id[GroupSet,KK]) = {
    Ajax.get(s"/api/groupSet/${id.id}", headers = Map("Accept" -> "application/json")).responseText.map(upickle.read[WithPerms[GroupSet]])
  }

  def latch(s:String) = cache.getOrElseUpdate(s, Latched.future(loadId(s.asId[GroupSet])))

}
