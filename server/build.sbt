ThisBuild / organization := "com.huntsman"
ThisBuild / scalaVersion := "3.1.2"

val circeVersion = "0.14.1"
val catsEffectVersion = "3.3.12"
val doobieVersion = "1.0.0-RC1"
val http4sVersion = "1.0.0-M34"

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
    "com.typesafe" % "config" % "1.4.2",
    "com.softwaremill.sttp.client3" %% "core" % "3.6.2",
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats" % "3.7.1",
    "org.tpolecat" %% "doobie-core" % doobieVersion,
    "org.xerial" % "sqlite-jdbc" % "3.36.0.3",
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "org.http4s" %% "http4s-ember-server" % http4sVersion,
    "org.http4s" %% "http4s-ember-client" % http4sVersion,
    "org.http4s" %% "http4s-circe" % http4sVersion,
    "org.slf4j" % "slf4j-simple" % "1.7.36",
    "org.jsoup" % "jsoup" % "1.15.2"
  )
)
