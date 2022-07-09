ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "3.1.2"

val circeVersion = "0.14.1"
val catsEffectVersion = "3.3.12"
val doobieVersion = "1.0.0-RC1"

lazy val root = (project in file(".")).settings(
  name := "skillfull-huntsman",
  libraryDependencies ++= Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect" % catsEffectVersion,
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "org.typelevel" %% "cats-effect-kernel" % catsEffectVersion,
    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std" % catsEffectVersion,
    "org.typelevel" %% "cats-effect-testing-scalatest" % "1.4.0" % Test,
    "com.softwaremill.sttp.client3" %% "core" % "3.6.2",
    "org.tpolecat" %% "doobie-core" % doobieVersion,
    "org.xerial" % "sqlite-jdbc" % "3.36.0.3",
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion
  )
)
