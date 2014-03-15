scalaVersion in ThisBuild := "2.10.3"

organization in ThisBuild := "com.impressory"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

resolvers in ThisBuild += "typesafe snaps" at "http://repo.typesafe.com/typesafe/snapshots/"

resolvers in ThisBuild += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

resolvers in ThisBuild  += "sonatype snaps" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers in ThisBuild  += "sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers in ThisBuild += "bintrayW" at "http://dl.bintray.com/wbillingsley/maven"

resolvers in ThisBuild  += DefaultMavenRepository

resolvers in ThisBuild  += JavaNet1Repository

