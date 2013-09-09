libraryDependencies ++= Seq(
    "com.wbillingsley" %% "handy-appbase-core" % "0.4",    
	"com.wbillingsley" %% "handy-reactivemongo" % "0.4",
	"com.wbillingsley" %% "handy-play" % "0.4",
	"org.reactivemongo" %% "reactivemongo" % "0.9",
	"org.reactivemongo" %% "play2-reactivemongo" % "0.9",
    "org.specs2" %% "specs2" % "2.2" % "test"
)

libraryDependencies <+= scalaVersion(sv => "org.scala-lang" % "scala-reflect" % sv)

parallelExecution in Test := false