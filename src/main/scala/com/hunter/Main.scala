package com.hunter

import cats.syntax.all._
import cats.effect._
import scraper.JustJoinIt.JustJoinItScraper
import domain.ExperienceLevel
import database.{createTransactor, save}
import doobie.implicits._
import com.typesafe.config._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    val conf = ConfigFactory.load("credentials")

    val transactor = createTransactor(
      pathToDatabase = conf.getString("database.path"),
      username = conf.getString("database.username"),
      password = conf.getString("database.password")
    )

    for {
      offers <- JustJoinItScraper.getOffers("Scala")(ExperienceLevel.Junior)
      dbResults <- offers
        .map(offer => save(offer).transact(transactor))
        .sequence
      _ <- IO.println(s"Saved ${dbResults.length} items.")
    } yield ExitCode.Success
  }
}
