package com.assessory.sjsreact

import com.assessory.api.Task
import com.assessory.sjsreact.services.{TaskService, CourseService}
import com.wbillingsley.handy.Id
import Id._
import com.wbillingsley.handy.appbase.{Group, Course}
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra._
import japgolly.scalajs.react.extra.router2._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.window

import scala.language.implicitConversions

object MainRouter {

  val base = "#!/"

  implicit val baseUrl:BaseUrl = BaseUrl.fromWindowOrigin / base


  sealed trait MyPages
  case object Home                extends MyPages {
    val path = base
  }
  case object LogIn               extends MyPages {
    val relpath = "logIn"
    val path = base + relpath
  }
  case object SignUp              extends MyPages {
    val relpath = "signUp"
    val path = base + relpath
  }

  case object CreateCourseP       extends MyPages {
    val relPath = "createCourse"
    val path = base + relPath
  }

  case class CourseP(id:String)    extends MyPages
  object CourseP {
    implicit def toId(c:CourseP):Id[Course,String] = Id(c.id)
    def pathRel(c:Id[Course,String]) = s"course/${c.id}"
    def path(c:Id[Course,String]) = base + pathRel(c)
  }

  case class TaskP(id:String)      extends MyPages
  object TaskP {
    implicit def toId(c:TaskP):Id[Task,String] = Id(c.id)
    def pathRel(c:Id[Task,String]) = s"task/${c.id}"
    def path(c:Id[Task,String]) = base + pathRel(c)
  }

  case class GroupP(id:String)      extends MyPages
  object GroupP {
    implicit def toId(c:GroupP):Id[Group,String] = Id(c.id)
    def pathRel(c:Id[Group,String]) = s"group/${c.id}"
    def path(c:Id[Group,String]) = base + pathRel(c)
  }


  val routerConfig = RouterConfigDsl[MyPages].buildConfig { dsl =>
    import dsl._

    val key = string("[a-z0-9]+")

    (
      emptyRule
      | staticRoute(root, Home) ~> render(Front.front())
      | staticRoute(LogIn.relpath, LogIn) ~> render(LogInViews.logIn())
      | staticRoute(SignUp.relpath, SignUp) ~> render(LogInViews.signUp())
      | staticRoute(CreateCourseP.relPath, CreateCourseP) ~> render(CourseViews.createCourse())
      | dynamicRouteCT[CourseP]("course" / key.caseClass[CourseP]) ~> dynRender(CourseViews.courseFront(_))
      | dynamicRouteCT("task" / key.caseClass[TaskP]) ~> dynRender(TaskViews.taskFront(_))

    ).notFound(redirectToPage(Home)(Redirect.Replace))
  }

  // Set up the router component, assuming / to be the root URL
  val (router, logic) = Router.componentAndLogic(baseUrl, routerConfig)

  def goTo(p:MyPages) = logic.ctl.set(p).unsafePerformIO()
}
