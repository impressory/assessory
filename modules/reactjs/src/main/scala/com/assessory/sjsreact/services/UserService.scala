package com.assessory.sjsreact.services

import com.assessory.clientpickle.Pickles._
import com.assessory.sjsreact.{Latched, WebApp}
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.{Course, User}
import org.scalajs.dom.ext.{AjaxException, Ajax}

import scala.collection.mutable
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

object UserService {

  val cache = mutable.Map.empty[String, Ref[User]]

  val self = Latched.future(
    Ajax.post("/api/self", headers = Map("Accept" -> "application/json")).responseText.map(upickle.read[User]).optional404
  ).onChange(_ => WebApp.rerender())

  def logOut():Unit = {
    Ajax.post("/api/logOut", headers = Map("Accept" -> "application/json")).andThen{ case _ => self.fill(None) }
  }

  def loadId[KK <: String](id:Id[User,KK]) = new RefFutureOption(
    Ajax.get(s"/api/user/${id.id}", headers=Map("Accept" -> "application/json")).responseText.map(upickle.read[User]).optional404
  )

  val lu = new LookUp[User, String] {
    override def one[KK <: String](r: Id[User, KK]): Ref[User] = cache.getOrElseUpdate(r.id, loadId(r))

    override def many[KK <: String](r: Ids[User, KK]): RefMany[User] = ???
  }

}
