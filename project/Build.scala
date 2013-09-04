import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {
  
    val appName         = "assessory"
    val appVersion      = "0.1"
     
    lazy val assessoryApi = Project(appName + "-api", base = file ("modules/api"))
      
    lazy val assessoryReactivemongo = Project(appName + "-reactivemongo", base = file ("modules/reactivemongo")).dependsOn(assessoryApi)
    
    val appBaseDependencies = Seq(
      "com.wbillingsley" %% "handy" % "0.4-SNAPSHOT",
      "com.wbillingsley" %% "handy-appbase-core" % "0.4-SNAPSHOT",
      "com.wbillingsley" %% "eventroom" % "0.1-SNAPSHOT"
    )
    
    lazy val appBase = play.Project(appName + "-app-base", appVersion, appBaseDependencies, path= file("modules/app-base")).settings(
    
    )
      
    val appDependencies = Seq(
      "com.wbillingsley" %% "handy-appbase-core" % "0.4-SNAPSHOT",
      "com.wbillingsley" %% "eventroom" % "0.1-SNAPSHOT",
      "com.wbillingsley" %% "handy-play-oauth" % "0.1-SNAPSHOT",
      "net.sf.opencsv" % "opencsv" % "2.0"
    )

  lazy val aaaMain = play.Project(appName, appVersion, appDependencies).settings(

    templatesImport += "com.wbillingsley.handy._",

    resolvers ++= Seq(
        "handy releases" at "https://bitbucket.org/wbillingsley/mavenrepo/raw/master/releases/",
        "handy snapshots" at "https://bitbucket.org/wbillingsley/mavenrepo/raw/master/snapshots/",
        "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
    ),

    requireJs ++= Seq(
          "main.js" 
    )
    
        // Add your own project settings here      
  ).dependsOn(
      assessoryApi,
      assessoryReactivemongo
  )

}
