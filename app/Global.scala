import com.wbillingsley.handy._
import play.api._
import Play.current
import play.api.mvc.AcceptExtractors
import com.wbillingsley.handy.RefFuture
import com.assessory.reactivemongo._
import com.wbillingsley.handy.appbase.DataAction

object Global extends GlobalSettings with AcceptExtractors {
  
  override def onStart(app: Application) {
    
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
    
    val lookupPf = UserDAO.lookupPf orElse 
                   CourseDAO.lookupPf orElse
                   GroupSetDAO.lookupPf orElse
                   GroupDAO.lookupPf orElse
                   PreenrolDAO.lookupPf orElse
                   GPreenrolDAO.lookupPf orElse
                   TaskDAO.lookupPf orElse
                   TaskOutputDAO.lookupPf orElse
                   GroupCritAllocationDAO.lookupPf
    
    val lookupManyPf = UserDAO.lookupManyPf orElse
                       CourseDAO.lookupManyPf orElse
                       GroupSetDAO.lookupManyPf orElse
                       GroupDAO.lookupManyPf orElse
                       PreenrolDAO.lookupManyPf orElse
                       GPreenrolDAO.lookupManyPf orElse
                       TaskDAO.lookupManyPf orElse
                       TaskOutputDAO.lookupManyPf orElse
                       GroupCritAllocationDAO.lookupManyPf

    // Set the home action
    DataAction.homeAction = com.assessory.play.controllers.Application.index
      
    RefById.lookUpMethod = new RefById.LookUp {      
      def lookup[T](r: RefById[T, _]) = 
        lookupPf.apply(r).asInstanceOf[Ref[T]]
    }
    
    RefManyById.lookUpMethod = new RefManyById.LookUp {
      def lookup[T](r: RefManyById[T, _]) = 
        lookupManyPf.apply(r).asInstanceOf[RefMany[T]]
    }

  }

}