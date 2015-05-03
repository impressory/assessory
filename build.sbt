

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

lazy val assessoryApi = (crossProject.crossType(CrossType.Pure) in file("modules/api"))
  .settings(commonSettings:_*)
  .settings(
    libraryDependencies ++= Seq(
      "com.wbillingsley" %%% "handy" % "0.7.0-SNAPSHOT",
      "com.wbillingsley" %%% "handy-appbase" % "0.7.0-SNAPSHOT"
    )
  )

lazy val assessoryApiJVM = assessoryApi.jvm
lazy val assessoryApiJS = assessoryApi.js

lazy val clientPickle = (crossProject.crossType(CrossType.Pure) in file("modules/clientPickle"))
  .settings(commonSettings:_*)
  .settings(
    libraryDependencies ++= Seq(
      // Pickling
      "com.lihaoyi" %%% "upickle" % "0.2.8"
    )
  )
  .dependsOn(assessoryApi)

lazy val clientPickleJVM = clientPickle.jvm
  .settings(
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2" % "2.3.12" % "test"
    )
  )

lazy val clientPickleJS = clientPickle.js

lazy val assessoryAsyncMongo = project.in(file("modules/asyncmongo"))
  .settings(commonSettings:_*)
  .dependsOn(assessoryApiJVM)

lazy val assessoryModel = project.in(file("modules/model"))
  .settings(commonSettings:_*)
  .dependsOn(assessoryApiJVM, assessoryAsyncMongo)

lazy val reactjs = project.in(file("modules/reactjs"))
  .settings(commonSettings:_*)
  .enablePlugins(ScalaJSPlugin)

lazy val assessory = project.in(file("modules/play"))
  .enablePlugins(PlayScala)
  .settings(commonSettings:_*)
  .dependsOn(assessoryApiJVM, clientPickleJVM, assessoryModel, assessoryAsyncMongo)
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
  .aggregate(assessoryApiJVM, clientPickleJVM, assessoryAsyncMongo, assessoryModel, assessory)

