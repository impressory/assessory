scalaVersion in ThisBuild := "2.11.1"

organization in ThisBuild := "com.impressory"

version in ThisBuild := "0.1-SNAPSHOT"

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature")

resolvers in ThisBuild ++= Seq(
  "typesafe snaps" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "sonatype snaps" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "bintrayW" at "http://dl.bintray.com/wbillingsley/maven",
  DefaultMavenRepository
)

name := "assessory"

lazy val assessory = project.in(file("."))
  .aggregate(assessoryApi, assessoryReactiveMongo)
  .dependsOn(assessoryApi, assessoryReactiveMongo)
  .enablePlugins(play.PlayScala)
  .settings(
    PlayKeys.routesImport ++= Seq(
      "com.wbillingsley.handy._",
      "com.assessory.api._",
      "com.assessory.play.PathBinders._",
      "scala.language.reflectiveCalls"
    )
  )

libraryDependencies ++= Seq(
  "com.wbillingsley" %% "handy" % "0.6.0-SNAPSHOT",
  "com.wbillingsley" %% "handy-user" % "0.6.0-SNAPSHOT",
  "com.wbillingsley" %% "handy-play" % "0.6.0-SNAPSHOT",
  "com.wbillingsley" %% "handy-play-oauth" % "0.3.0-SNAPSHOT",
  "net.sf.opencsv" % "opencsv" % "2.0",
  // JavaScript
  "org.webjars" %% "webjars-play" % "2.3.0",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "org.webjars" % "font-awesome" % "4.1.0",
  "org.webjars" % "angularjs" % "1.2.20",
  "org.webjars" % "marked" % "0.3.2-1"
)

pipelineStages := Seq(rjs, digest, gzip)

includeFilter in (Assets, LessKeys.less) := "main.less"

lazy val assessoryApi = project.in(file("modules/api"))

lazy val assessoryReactiveMongo = project.in(file("modules/reactivemongo")).dependsOn(assessoryApi)
