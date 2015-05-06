// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.8")

// Use the require.js minifier
addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")

// Use CoffeeScript
addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

// Use Less CSS
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.0")

// Use Twirl for compiling Angular templates together
addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.0.2")

// Scala.js for client-side components written in Scala
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.2")

// Brings the compiled Scala.js JavaScript into the Play project's assets
addSbtPlugin("com.vmunier" % "sbt-play-scalajs" % "0.2.4")


// --- DEV TOOLS ---

// Means sbt can show a tree structure of our dependencies
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.5")
