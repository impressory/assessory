import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {
  
    val appName         = "assessory"
    val appVersion      = "0.1-SNAPSHOT"
     
    lazy val assessoryApi = Project(appName + "-api", base = file ("modules/api")).settings(
    )
      
    lazy val assessoryReactivemongo = Project(appName + "-reactivemongo", base = file ("modules/reactivemongo")).dependsOn(assessoryApi).settings(
    )
    
    val appBaseDependencies = Seq(
      "com.wbillingsley" %% "handy" % "0.5.0-SNAPSHOT",
      "com.wbillingsley" %% "handy-appbase-core" % "0.5.0-SNAPSHOT"
    )
    
    lazy val appBase = play.Project(appName + "-app-base", appVersion, appBaseDependencies, path= file("modules/app-base")).settings(
    )
      
    val appDependencies = Seq(
      "com.wbillingsley" %% "handy-appbase-core" % "0.5.0-SNAPSHOT",
      "com.wbillingsley" %% "handy-play-oauth" % "0.2-SNAPSHOT",
      "net.sf.opencsv" % "opencsv" % "2.0"
    )

  lazy val mainProj = play.Project(appName, appVersion, appDependencies).settings(

    // Enable further details on compiler warnings 
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
    
    templatesImport += "com.wbillingsley.handy._",
    
    // Modular routes in Play 2.1 requires reflective calls
    routesImport ++= Seq("language.reflectiveCalls"),
    
    requireJs ++= Seq(
          "main.js" 
    )
    
  ).dependsOn(
      assessoryApi,
      assessoryReactivemongo
  )

  override def rootProject = Some(mainProj)
  
}
