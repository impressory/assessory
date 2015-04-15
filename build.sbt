

lazy val commonSettings = Seq(
  scalaVersion := "2.11.6",
  organization := "com.impressory",
  version := "0.2-SNAPSHOT",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
  resolvers ++= Seq(
    "typesafe snaps" at "http://repo.typesafe.com/typesafe/snapshots/",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    "sonatype snaps" at "https://oss.sonatype.org/content/repositories/snapshots/",
    "sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/",
    "bintrayW" at "http://dl.bintray.com/wbillingsley/maven",
    DefaultMavenRepository
  )
)

lazy val assessoryApi = project.in(file("modules/api"))
  .settings(commonSettings:_*)

lazy val assessoryReactiveMongo = project.in(file("modules/reactivemongo"))
  .settings(commonSettings:_*)
  .dependsOn(assessoryApi)

lazy val assessory = project.in(file("modules/play"))
  .enablePlugins(PlayScala)
  .settings(commonSettings:_*)
  .dependsOn(assessoryApi, assessoryReactiveMongo)
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
  .aggregate(assessoryApi, assessoryReactiveMongo, assessory)
