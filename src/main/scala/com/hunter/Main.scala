package com.hunter

import cats.syntax.all._
import cats.effect._
import doobie.implicits._
import com.typesafe.config._
import com.comcast.ip4s._
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.ember.server._
import scraper.JustJoinIt.JustJoinItScraper
import domain.ExperienceLevel
import database.{createTransactor, save}
import com.hunter.entrypoints.ScrapeService

object Main extends IOApp {
  private val conf = ConfigFactory.load("credentials")

  private val transactor = createTransactor(
    pathToDatabase = conf.getString("database.path"),
    username = conf.getString("database.username"),
    password = conf.getString("database.password")
  )

  private val scrapers = List(JustJoinItScraper)

  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(ScrapeService.init(transactor, scrapers))
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
