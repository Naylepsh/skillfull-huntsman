ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "3.1.2"

val circeVersion = "0.14.1"

lazy val root = (project in file(".")).settings(
  name := "job-hunter",
  libraryDependencies ++= Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect" % "3.3.12",
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "org.typelevel" %% "cats-effect-kernel" % "3.3.12",
    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std" % "3.3.12",
    "org.typelevel" %% "cats-effect-testing-scalatest" % "1.4.0" % Test,
    "com.softwaremill.sttp.client3" %% "core" % "3.6.2",
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion
  )
)

scalacOptions += "-Ypartial-unification"
