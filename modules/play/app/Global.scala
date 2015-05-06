import com.assessory.asyncmongo._
import com.assessory.model.DoWiring
import com.wbillingsley.handy.RefFuture
import play.api.Play.current
import play.api._
import play.api.mvc.AcceptExtractors

object Global extends GlobalSettings with AcceptExtractors {

  override def onStart(app: Application) {
    wire()
  }

  def wire() = {
    // Set up the database
    DB.dbName = Play.configuration.getString("mongo.dbname").getOrElse("assessory_2015_1")
    DB.connectionString = Play.configuration.getString("mongo.connection").getOrElse("mongodb://localhost:27017")
    DB.dbUser = Play.configuration.getString("mongo.dbuser")
    DB.dbPwd = Play.configuration.getString("mongo.dbpwd")

    // Set the execution context (ie the thread pool) that RefFuture work should happen on
    RefFuture.executionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

    // Set the completion action for OAuth
    com.wbillingsley.handy.playoauth.PlayAuth.onAuth = { r =>
      com.assessory.auth.controllers.InterstitialController.onOAuth(r)
    }

    // Wire up the lookups
    DoWiring.doWiring
  }


  /**
   * We have many routes that only exist on the client side.
   */
  override def onHandlerNotFound(request:play.api.mvc.RequestHeader) = {
    import play.api.libs.json.Json
    import play.api.mvc.Results

    import scala.concurrent.Future

    request match {
      case Accepts.Html() => super.onHandlerNotFound(request)
      case Accepts.Json() => Future.successful(Results.NotFound(Json.obj("error" -> "not found")))
      case _ => Future.successful(Results.NotFound)
    }
  }

}
