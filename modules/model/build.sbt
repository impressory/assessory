libraryDependencies ++= Seq(
  "com.wbillingsley" %% "handy-mongodb-async" % "0.7.0-SNAPSHOT",
  "net.sf.opencsv" % "opencsv" % "2.0",
  "org.specs2" %% "specs2" % "2.3.12" % "test"
)

parallelExecution in Test := false

