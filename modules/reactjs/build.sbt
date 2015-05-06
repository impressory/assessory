libraryDependencies ++= Seq(
  "com.github.japgolly.scalajs-react" %%% "core" % "0.8.4",
  "com.github.japgolly.scalajs-react" %%% "extra" % "0.8.4"
)

jsDependencies += "org.webjars" % "react" % "0.12.1" / "react-with-addons.js" commonJSName "React"
