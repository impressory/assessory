package com.assessory.sjsreact.services

import com.assessory.api.client.WithPerms
import com.assessory.clientpickle.Pickles._
import com.assessory.sjsreact.Latched
import com.wbillingsley.handy.{Ids, Id}
import Id._
import Ids._
import com.wbillingsley.handy.appbase.{Group, Course}
import org.scalajs.dom.ext.Ajax

import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.util.{Success, Failure}

object GroupService {

  val cache = mutable.Map.empty[String, Latched[WithPerms[Group]]]

  val myGroups = Latched.lazily(
    Ajax.post(s"/api/group/my", headers=AJAX_HEADERS).responseText.map(upickle.read[Seq[WithPerms[Group]]])
  )
  UserService.self.listeners.add { _ => myGroups.clear(); cache.clear() }

  def myGroupsInCourse(courseId:Id[Course,String]) = Latched.lazily(
    Ajax.post(s"/api/course/${courseId.id}/group/my", headers=AJAX_HEADERS).responseText.map(upickle.read[Seq[WithPerms[Group]]])
  )

  def loadId[KK <: String](id:Id[Group,KK]) = {
    Ajax.get(s"/api/group/${id.id}", headers=AJAX_HEADERS).responseText.map(upickle.read[WithPerms[Group]])
  }

  def latch(s:String):Latched[WithPerms[Group]] = latch(s.asId)

  def latch(id:Id[Group,String]):Latched[WithPerms[Group]] = cache.getOrElseUpdate(id.id, Latched.lazily(loadId(id)))

  def preload[KK <: String](ids:Ids[Group,KK]) = {
    val idStrings:Seq[String] = ids.ids
    val missing = idStrings.filterNot(id => cache.contains(id))
    val promiseMap = (for (id <- missing) yield {
      val p = Promise[WithPerms[Group]]()
      cache.put(id, Latched.lazily(p.future))
      id -> p
    }).toMap

    val loading = Ajax.post("/api/group/findMany", upickle.write(missing.asIds[Group]), headers=AJAX_HEADERS)
      .responseText.map(upickle.read[Seq[WithPerms[Group]]])

    loading.andThen {
      case Success(seq) => for (g <- seq) promiseMap(g.item.id.id).complete(Success(g))
      case Failure(t) => for (p <- promiseMap.values) p.complete(Failure(t))
    }
  }

  def loadIds[KK <: String](ids:Ids[Group,KK]):Future[Seq[WithPerms[Group]]] = {
    preload(ids)
    ids.ids.map(id => cache(id).request).foldLeft(Future.successful(mutable.Buffer.empty[WithPerms[Group]])) { case (fbuf, f) =>
      for (buf <- fbuf; v <- f) yield { buf.append(v); buf }
    }
  }

  def latch(ids:Ids[Group,String]) = Latched.lazily(loadIds(ids))


}
