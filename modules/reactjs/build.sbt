libraryDependencies ++= Seq(
  "com.github.japgolly.scalajs-react" %%% "core" % "0.9.2",
  "com.github.japgolly.scalajs-react" %%% "extra" % "0.9.2"
)

jsDependencies += "org.webjars" % "react" % "0.12.1" / "react-with-addons.js" commonJSName "React"
