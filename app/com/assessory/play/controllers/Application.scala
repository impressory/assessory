package com.assessory.play.controllers

import play.api.mvc.{Action, Controller}
import com.wbillingsley.handy.appbase.DataAction
import com.assessory.reactivemongo.UserDAO

object Application extends Controller {
  
  /**
   * The HTML and Javascript for the client side of the app.
   * This also ensures the user's Play session cookie includes
   * a value for sessionKey.
   */
  def index = DataAction.forceSession(
    Action { 
      Ok(views.html.main())  
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
      case "course/createPreenrol.html" => Ok(views.html.partials.course.createPreenrol())
      case "course/viewPreenrol.html" => Ok(views.html.partials.course.viewPreenrol())

      case "group/createGroupSet.html" => Ok(views.html.partials.group.createGroupSet())
      case "group/viewGroupSet.html" => Ok(views.html.partials.group.viewGroupSet())

      case _ => NotFound(s"No such partial template: $templ")
    }
  }  
  
}