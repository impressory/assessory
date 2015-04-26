
resolvers += Resolver.file("Local repo", file("/home/user/.ivy2/local"))

libraryDependencies ++= Seq(
    "com.wbillingsley" %% "handy-appbase" % "0.7.0-SNAPSHOT",
    "com.wbillingsley" %% "handy-user" % "0.7.0-SNAPSHOT",
    "com.wbillingsley" %% "handy-play" % "0.7.0-SNAPSHOT",
    "net.sf.opencsv" % "opencsv" % "2.0",
    "org.specs2" %% "specs2" % "2.4.1" % "test"
)
