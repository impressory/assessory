package com.assessory.sjsreact.services

import com.assessory.api.client.EmailAndPassword
import com.assessory.clientpickle.Pickles._
import com.assessory.sjsreact.{MainRouter, Latched, WebApp}
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.{Course, User}
import japgolly.scalajs.react.extra.router.Redirect
import org.scalajs.dom.ext.{AjaxException, Ajax}

import scala.collection.mutable
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.util.Success

object UserService {

  val cache = mutable.Map.empty[String, Ref[User]]

  val self = Latched.lazily(
    Ajax.post("/api/self", headers=AJAX_HEADERS).responseText.map(upickle.read[User]).optional404
  )

  def logOut():Unit = {
    Ajax.post("/api/logOut", headers=AJAX_HEADERS).andThen{
      case _ =>
        self.fill(None)
        MainRouter.goTo(MainRouter.root)
        WebApp.rerender()
    }
  }

  def logIn(ep:EmailAndPassword):Future[User] = {
    Ajax.post("/api/logIn", upickle.write(ep), headers=AJAX_HEADERS).responseText.map(upickle.read[User]).andThen {
      case Success(u) =>
        self.fill(Some(u))
        MainRouter.goTo(MainRouter.root)
        WebApp.rerender()
    }
  }

  def signUp(ep:EmailAndPassword):Future[User] = {
    Ajax.post("/api/signUp", upickle.write(ep), headers=AJAX_HEADERS).responseText.map(upickle.read[User]).andThen{
      case Success(u) =>
        self.fill(Some(u))
        MainRouter.goTo(MainRouter.root)
        WebApp.rerender()
    }
  }

  def loadId[KK <: String](id:Id[User,KK]) = new RefFutureOption(
    Ajax.get(s"/api/user/${id.id}", headers=AJAX_HEADERS).responseText.map(upickle.read[User]).optional404
  )

  val lu = new LookUp[User, String] {
    override def one[KK <: String](r: Id[User, KK]): Ref[User] = cache.getOrElseUpdate(r.id, loadId(r))

    override def many[KK <: String](r: Ids[User, KK]): RefMany[User] = ???
  }

}
