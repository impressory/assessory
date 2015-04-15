import com.wbillingsley.handy._
import play.api._
import Play.current
import play.api.mvc.AcceptExtractors
import com.wbillingsley.handy.RefFuture
import com.assessory.reactivemongo._

object Global extends GlobalSettings with AcceptExtractors {
  
  override def onStart(app: Application) {
    wire()
  }

  def wire() = {
    // Set up the database
    DBConnector.dbName = Play.configuration.getString("mongo.dbname").getOrElse("assessory")
    DBConnector.connectionString = Play.configuration.getString("mongo.connection").getOrElse("localhost:27017")
    DBConnector.dbUser = Play.configuration.getString("mongo.dbuser")
    DBConnector.dbPwd = Play.configuration.getString("mongo.dbpwd")

    // Set the execution context (ie the thread pool) that RefFuture work should happen on
    RefFuture.executionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

    // Set the completion action for OAuth
    com.wbillingsley.handy.playoauth.PlayAuth.onAuth = { r =>
      com.assessory.auth.controllers.InterstitialController.onOAuth(r)
    }


    // Wire up the lookups
    import com.assessory.api.wiring.Lookups

    Lookups.luCourse = CourseDAO.LookUp
    Lookups.luCritAlloc = CritAllocationDAO.LookUp
    Lookups.luGPreenrol = GPreenrolDAO.LookUp
    Lookups.luGroup = GroupDAO.LookUp
    Lookups.luGroupSet = GroupSetDAO.LookUp
    Lookups.luPreenrol = PreenrolDAO.LookUp
    Lookups.luTask = TaskDAO.LookUp
    Lookups.luTaskOutput = TaskOutputDAO.LookUp
    Lookups.luUser = UserDAO.LookUp
    Lookups.registrationProvider = RegistrationDAO
  }

  
  /**
   * We have many routes that only exist on the client side. 
   */
  override def onHandlerNotFound(request:play.api.mvc.RequestHeader) = {
    import scala.concurrent.Future
    import play.api.mvc.Results
    import play.api.libs.json.Json
    
    request match {
      case Accepts.Html() => super.onHandlerNotFound(request)
      case Accepts.Json() => Future.successful(Results.NotFound(Json.obj("error" -> "not found")))
      case _ => Future.successful(Results.NotFound)
    }
  }  

}