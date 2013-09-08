import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {
  
    val appName         = "assessory"
    val appVersion      = "0.1"
     
    // Define the additional repositories we're going to need in one place, to reuse it in all subprojects
    val extraResolvers = Seq(
        "handy releases" at "https://bitbucket.org/wbillingsley/mavenrepo/raw/master/releases/",
        "handy snapshots" at "https://bitbucket.org/wbillingsley/mavenrepo/raw/master/snapshots/",
        "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
    )
     
    lazy val assessoryApi = Project(appName + "-api", base = file ("modules/api")).settings(
      resolvers ++= extraResolvers
    )
      
    lazy val assessoryReactivemongo = Project(appName + "-reactivemongo", base = file ("modules/reactivemongo")).dependsOn(assessoryApi).settings(
      resolvers ++= extraResolvers
    )
    
    val appBaseDependencies = Seq(
      "com.wbillingsley" %% "handy" % "0.4-SNAPSHOT",
      "com.wbillingsley" %% "handy-appbase-core" % "0.4-SNAPSHOT",
      "com.wbillingsley" %% "eventroom" % "0.1-SNAPSHOT"
    )
    
    lazy val appBase = play.Project(appName + "-app-base", appVersion, appBaseDependencies, path= file("modules/app-base")).settings(
      resolvers ++= extraResolvers
    )
      
    val appDependencies = Seq(
      "com.wbillingsley" %% "handy-appbase-core" % "0.4-SNAPSHOT",
      "com.wbillingsley" %% "eventroom" % "0.1-SNAPSHOT",
      "com.wbillingsley" %% "handy-play-oauth" % "0.1-SNAPSHOT",
      "net.sf.opencsv" % "opencsv" % "2.0"
    )

  lazy val aaaMain = play.Project(appName, appVersion, appDependencies).settings(

    templatesImport += "com.wbillingsley.handy._",

    resolvers ++= extraResolvers,

    requireJs ++= Seq(
          "main.js" 
    )
    
  ).dependsOn(
      assessoryApi,
      assessoryReactivemongo
  )

}
