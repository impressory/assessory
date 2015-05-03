libraryDependencies ++= Seq(
  "com.wbillingsley" %% "handy-mongodb-async" % "0.7.0-SNAPSHOT",
  "com.wbillingsley" %% "handy-user" % "0.7.0-SNAPSHOT",
  "org.specs2" %% "specs2" % "2.3.12" % "test"
)

parallelExecution in Test := false
