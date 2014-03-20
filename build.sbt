import play.Project._

scalaVersion in ThisBuild := "2.10.3"

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

playScalaSettings

lazy val assessory = project.in(file("."))
  .aggregate(assessoryApi, assessoryReactiveMongo)
  .dependsOn(assessoryApi, assessoryReactiveMongo)

libraryDependencies ++= Seq(
  "com.wbillingsley" %% "handy-appbase-core" % "0.5.0-SNAPSHOT",
  "com.wbillingsley" %% "handy-play-oauth" % "0.2-SNAPSHOT",
  "net.sf.opencsv" % "opencsv" % "2.0"
)

templatesImport += "com.wbillingsley.handy._"

// Modular routes in Play 2.1 requires reflective calls
routesImport ++= Seq("language.reflectiveCalls")

requireJs ++= Seq(
)

lazy val assessoryApi = project.in(file("modules/api"))

lazy val assessoryReactiveMongo = project.in(file("modules/reactivemongo")).dependsOn(assessoryApi)
