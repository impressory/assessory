package com.assessory.play.controllers

import play.api.mvc.{Action, Controller}
import com.wbillingsley.handy.appbase.{HeaderInfo, WithHeaderInfo, DataAction}
import com.assessory.api.UserError
import com.wbillingsley.handy._
import Ref._

object Application extends Controller {

  /**
   * A response to all OPTIONS requests, to support CORS
   */
  def options(path:String) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      Status(200).itself,
      HeaderInfo(headers=Seq(
        "Access-Control-Allow-Origin" -> uiBaseUrl,
        "Access-Control-Allow-Headers" -> request.headers.get("Access-Control-Request-Headers").getOrElse("accept, content-type"),
        "Access-Control-Allow-Method" -> request.headers.get("Access-Control-Request-Method").getOrElse("GET, POST, OPTIONS"),
        "Access-Control-Allow-Credentials" -> "true"
      )).itself
    )
  }
  
  /**
   * The HTML and Javascript for the client side of the app.
   * This also ensures the user's Play session cookie includes
   * a value for sessionKey.
   */
  def index = DataAction.forceSession(
    Action { 
      Redirect(uiBaseUrl)
    }
  )
  
  /**
   * As we're using Angular.js, there may be routes (paths) in the browser
   * that don't correspond to routes on the server. In which case, if a user
   * hits refresh, we want to ensure they don't suddenly receive a 404 from the
   * server.
   * 
   * Instead, we have a default route that returns the index page.
   */
  def defaultRoute(path:String) = index

  /**
   * We put the partial templates into this method so that adding a partial 
   * template does not require editing the routes file. 
   * 
   * Editing the routes file would (in dev mode) cause a complete recompilation
   * of all sources, which takes much longer than just recompiling this controller
   */
  def partial(templ:String) = Action { 
    templ match {
      case "main.html" => Ok(views.html.partials.main())
      case "signUp.html" => Ok(views.html.partials.signUp())
      case "logIn.html" => Ok(views.html.partials.logIn())
      case "self.html" => Ok(views.html.partials.self())
      
      case "course/create.html" => Ok(views.html.partials.course.create())
      case "course/view.html" => Ok(views.html.partials.course.view())
      case "course/admin.html" => Ok(views.html.partials.course.admin())
      case "course/createPreenrol.html" => Ok(views.html.partials.course.createPreenrol())
      case "course/viewPreenrol.html" => Ok(views.html.partials.course.viewPreenrol())

      case "group/view.html" => Ok(views.html.partials.group.view())
      case "group/createGroupSet.html" => Ok(views.html.partials.group.createGroupSet())
      case "group/viewGroupSet.html" => Ok(views.html.partials.group.viewGroupSet())

      case "task/view.html" => Ok(views.html.partials.task.view())
      case "task/admin.html" => Ok(views.html.partials.task.admin())
      case "groupcrit/createTask.html" => Ok(views.html.partials.groupcrit.createTask())
      case "outputcrit/createTask.html" => Ok(views.html.partials.outputcrit.createTask())

      case "taskoutput/view.html" => Ok(views.html.partials.taskoutput.view())
      case "taskoutput/edit.html" => Ok(views.html.partials.taskoutput.edit())
      
      case _ => NotFound(s"No such partial template: $templ")
    }
  }
  
  def notFound = DataAction.returning.result { RefNone }
  
  implicit val utj = com.assessory.play.json.UserToJson
  import com.assessory.api.User
  def userError = DataAction.returning.one { implicit request => 
    
    import play.api.libs.concurrent.Execution.Implicits.defaultContext
    
    import com.wbillingsley.handy._
    def fut = new RefFuture[User](
      scala.concurrent.future {
        throw new UserError("Testing a user error")
      }
    )
    val fail1 = fut
    for (s <- fail1; q <- fut; r <- fail1) yield r
  }
  
}