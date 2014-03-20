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