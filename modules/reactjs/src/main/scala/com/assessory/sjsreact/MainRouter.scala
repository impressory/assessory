package com.assessory.sjsreact

import com.assessory.api.Task
import com.assessory.sjsreact.services.{TaskService, CourseService}
import com.wbillingsley.handy.Id
import Id._
import com.wbillingsley.handy.appbase.Course
import japgolly.scalajs.react.extra.router.{BaseUrl, Router, RoutingRules}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.window

object MainRouter extends RoutingRules {

  val base = "#!"

  implicit val baseUrl:BaseUrl = BaseUrl.fromWindowOrigin / base

  val root:MainRouter.Loc = register(rootLocation(Front.front))

  // Register route for log in
  val logInRel = "/logIn"
  val logIn = base + logInRel
  register(location(logInRel, LogInViews.logIn))

  // Register route for viewing courses
  val courseRgx = "^/course/(.+)$".r
  def courseHomeRel(c:Id[Course,String]) = s"/course/${c.id}"
  def courseHome(c:Id[Course,String]) = base + courseHomeRel(c)
  val courseHomeL = dynLink[Id[Course,String]](courseHome)
  register(parser({ case courseRgx(n) => n }).location(n => CourseViews.courseFront(CourseService.latch(n))))

  // Register route for viewing tasks
  val taskRgx = "^/task/(.+)$".r
  def taskHomeRel(c:Id[Task,String]) = s"/task/${c.id}"
  def taskHome(c:Id[Task,String]) = base + taskHomeRel(c)
  val taskHomeL = dynLink[Id[Task,String]](taskHome)
  register(parser({ case taskRgx(n) => n }).location(n => TaskViews.taskView(TaskService.latch(n))))



  // functions to provide links (<a href...>) to routes
  def rootLink = router.link(root)
  def routerLink(loc: Loc) = router.link(loc)

  def goTo(loc:Loc) = {
    println("Sync to " + loc)
    window.location.assign(loc.path.abs(baseUrl).value)
    WebApp.rerender()
  }


  // Set up the router component, assuming / to be the root URL
  val router = routingEngine(baseUrl, Router.consoleLogger)
  val routerComponent = Router.component(router)

  override protected val notFound: MainRouter.DynAction = render( <.h1("404, Not Found.") )
}
