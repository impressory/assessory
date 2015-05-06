package com.assessory.sjsreact

import com.assessory.sjsreact.services.CourseService
import com.wbillingsley.handy.Id
import Id._
import com.wbillingsley.handy.appbase.Course
import japgolly.scalajs.react.extra.router.{BaseUrl, Router, RoutingRules}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object MainRouter extends RoutingRules {

  val base = "#!"

  val root:MainRouter.Loc = register(rootLocation(Front.front))

  val courseRgx = "^/course/(.+)$".r
  def courseHomeRel(c:Id[Course,String]) = s"/course/${c.id}"
  def courseHome(c:Id[Course,String]) = base + courseHomeRel(c)
  val courseHomeL = dynLink[Id[Course,String]](courseHome)
  register(parser({ case courseRgx(n) => n }).location(n => CourseViews.courseFront(CourseService.latch(n))))


  // functions to provide links (<a href...>) to routes
  def rootLink = router.link(root)
  def routerLink(loc: Loc) = router.link(loc)

  // Set up the router component, assuming / to be the root URL
  val router = routingEngine(BaseUrl.fromWindowOrigin / base, Router.consoleLogger)
  val routerComponent = Router.component(router)

  override protected val notFound: MainRouter.DynAction = render( <.h1("404, Not Found.") )
}
