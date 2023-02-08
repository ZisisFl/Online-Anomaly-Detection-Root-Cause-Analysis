val scalaMainVersion = "2.12"
val flinkVersion = "1.13.2"

name := "online-rca"
organization := "auth.dws"
version := "0.1"
ThisBuild / scalaVersion := scalaMainVersion + ".12"

val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-connector-kafka" % flinkVersion,
  "org.apache.flink" %% "flink-test-utils" % flinkVersion % Test
)

lazy val root = (project in file("."))
  .settings(
    libraryDependencies ++= flinkDependencies,
    libraryDependencies += "com.typesafe" % "config" % "1.4.2",
    libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.3.0-SNAP3" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.3.0-SNAP3" % Test
  )

// entry point
assembly / mainClass := Some("jobs.MainJob")

// make run command include the provided dependencies
Compile / run  := Defaults.runTask(
  Compile / fullClasspath,
  Compile / run / mainClass,
  Compile / run / runner
).evaluated

// stays inside the sbt console when we press "ctrl-c" while a Flink programme executes with "run" or "runMain"
Compile / run / fork := true
Global / cancelable := true

// exclude Scala library from assembly
assembly / assemblyOption := (assembly / assemblyOption).value.copy(includeScala = false)