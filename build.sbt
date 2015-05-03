

lazy val commonSettings = Seq(
  scalaVersion := "2.11.6",
  organization := "com.impressory",
  version := "0.2-SNAPSHOT",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
  resolvers ++= Seq(
    Resolver.file("Local repo", file("/home/user/.ivy2/local")),
    "sonatype snaps" at "https://oss.sonatype.org/content/repositories/snapshots/",
    "typesafe snaps" at "http://repo.typesafe.com/typesafe/snapshots/",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    "sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/",
    "bintrayW" at "http://dl.bintray.com/wbillingsley/maven",
    DefaultMavenRepository
  )
)

lazy val assessoryApi = project.in(file("modules/api"))
  .settings(commonSettings:_*)

lazy val assessoryAsyncMongo = project.in(file("modules/asyncmongo"))
  .settings(commonSettings:_*)
  .dependsOn(assessoryApi)

lazy val assessoryModel = project.in(file("modules/model"))
  .settings(commonSettings:_*)
  .dependsOn(assessoryApi, assessoryAsyncMongo)

lazy val reactjs = project.in(file("modules/reactjs"))
  .settings(commonSettings:_*)
  .enablePlugins(ScalaJSPlugin)

lazy val assessory = project.in(file("modules/play"))
  .enablePlugins(PlayScala)
  .settings(commonSettings:_*)
  .dependsOn(assessoryApi, assessoryModel, assessoryAsyncMongo)
  .settings(
    PlayKeys.routesImport ++= Seq(
      "com.wbillingsley.handy._",
      "com.assessory.api._",
      "com.assessory.play.PathBinders._",
      "scala.language.reflectiveCalls"
    )
  )

lazy val aggregate = project.in(file("."))
  .settings(commonSettings:_*)
  .aggregate(assessoryApi, assessoryAsyncMongo, assessoryModel, assessory)

