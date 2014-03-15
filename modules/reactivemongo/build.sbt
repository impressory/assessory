libraryDependencies ++= Seq(
  "com.wbillingsley" %% "handy-user" % "0.5.0-SNAPSHOT",
  "com.wbillingsley" %% "handy-appbase-core" % "0.5.0-SNAPSHOT",
  "com.wbillingsley" %% "handy-reactivemongo" % "0.5.0-SNAPSHOT",
  "com.wbillingsley" %% "handy-play" % "0.5.0-SNAPSHOT",
  "org.reactivemongo" %% "reactivemongo" % "0.10.0" exclude("org.scala-stm", "scala-stm_2.10.0"),
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.0" exclude("org.scala-stm", "scala-stm_2.10.0"),
  "org.specs2" %% "specs2" % "2.2" % "test"
)

libraryDependencies <+= scalaVersion(sv => "org.scala-lang" % "scala-reflect" % sv)

parallelExecution in Test := false