package com.assessory.sjsreact.services

import com.assessory.api.client.WithPerms
import com.assessory.clientpickle.Pickles._
import com.assessory.sjsreact.Latched
import com.wbillingsley.handy.Id
import Id._
import com.wbillingsley.handy.appbase.{Group, Course}
import org.scalajs.dom.ext.Ajax

import scala.collection.mutable
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

object GroupService {

  val cache = mutable.Map.empty[String, Latched[WithPerms[Group]]]

  def myGroups(courseId:Id[Course,String]) = Latched.future(
    Ajax.post(s"/api/course/${courseId.id}/group/my", headers = Map("Accept" -> "application/json")).responseText.map(upickle.read[Seq[Group]])
  )

  def loadId[KK <: String](id:Id[Group,KK]) = {
    Ajax.get(s"/api/group/${id.id}", headers = Map("Accept" -> "application/json")).responseText.map(upickle.read[WithPerms[Group]])
  }

  def latch(s:String) = cache.getOrElseUpdate(s, Latched.future(loadId(s.asId[Group])))

}
