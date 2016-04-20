import sbt.Project.projectToRef
import java.nio.charset.Charset

name := "RecursiveParserWeb"

version := "1.0"

lazy val clients = Seq(client)
lazy val scalaV = "2.11.7"

lazy val server = (project in file("server")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd, gzip),
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  libraryDependencies ++= Seq(
    "com.vmunier" %% "play-scalajs-scripts" % "0.4.0",
    "org.webjars" % "jquery" % "2.2.1",
    "org.webjars" % "d3js" % "3.5.12",
    "org.webjars.npm" % "dagre-d3" % "0.4.17",
    "org.webjars" % "bootstrap" % "3.3.6",
    specs2 % Test
  )
).enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(sharedJvm)

val electronMainPath = SettingKey[File]("electron-main-path", "The absolute path where to write the Electron application's main.")

val electronMain = TaskKey[File]("electron-main", "Generate Electron application's main file.")

lazy val client = (project in file("client")).settings(
  scalaVersion := scalaV,
  persistLauncher in Compile := true,
  persistLauncher in Test := false,

  electronMainPath := {
    baseDirectory.value / "electron-app" / "main.js"
  },

  electronMain := {
    // TODO here we rely on the files written on disk but it would be better to be able to get the actual content directly
    // from the tasks. I am not sure it's possible just yet though.
    val jsCode: String = IO.read((fastOptJS in Compile).value.data, Charset.forName("UTF-8"))
    val launchCode = IO.read((packageScalaJSLauncher in Compile).value.data, Charset.forName("UTF-8"))
    // we don't need jsDeps here but want it to be generated anyway so that we can start the Electron app right away
    val jsDeps = (packageJSDependencies in Compile).value

    // hack to get require and __dirname to work in the main process
    // see https://gitter.im/scala-js/scala-js/archives/2015/04/25
    val hack =
      """
      var addGlobalProps = function(obj) {
        obj.require = require;
        obj.__dirname = __dirname;
      }
      if((typeof __ScalaJSEnv === "object") && typeof __ScalaJSEnv.global === "object") {
        addGlobalProps(__ScalaJSEnv.global);
      } else if(typeof  global === "object") {
        addGlobalProps(global);
      } else if(typeof __ScalaJSEnv === "object") {
        __ScalaJSEnv.global = {};
        addGlobalProps(__ScalaJSEnv.global);
      } else {
        var __ScalaJSEnv = { global: {} };
        addGlobalProps(__ScalaJSEnv.global)
      }
      """

    val dest = electronMainPath.value
    IO.write(dest, hack + jsCode + launchCode, Charset.forName("UTF-8"))
    dest
  },

  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.0",
    "be.doeraene" %%% "scalajs-jquery" % "0.9.0",
    "org.singlespaced" %%% "scalajs-d3" % "0.3.1"
  ),

  jsDependencies += RuntimeDOM,
  skip in packageJSDependencies := false
).enablePlugins(ScalaJSPlugin).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(scalaVersion := scalaV).
  jsConfigure(_ enablePlugins ScalaJSPlay)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
