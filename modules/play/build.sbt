pipelineStages := Seq(rjs, digest, gzip)

includeFilter in (Assets, LessKeys.less) := "main.less"


libraryDependencies ++= Seq(
  "com.wbillingsley" %% "handy" % "0.7.0-SNAPSHOT",
  "com.wbillingsley" %% "handy-user" % "0.7.0-SNAPSHOT",
  "com.wbillingsley" %% "handy-play" % "0.7.0-SNAPSHOT",
  "com.wbillingsley" %% "handy-play-oauth" % "0.3.0-SNAPSHOT",
  "net.sf.opencsv" % "opencsv" % "2.0",

  // Pickling
  "com.lihaoyi" %% "upickle" % "0.2.8",

  // JavaScript
  "org.webjars" %% "webjars-play" % "2.3.0",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "org.webjars" % "font-awesome" % "4.1.0",
  "org.webjars" % "angularjs" % "1.2.20",
  "org.webjars" % "angular-ui-router" % "0.2.10-1",
  "org.webjars" % "angular-ui-bootstrap" % "0.11.0-2",
  "org.webjars" % "marked" % "0.3.2-1"
)


