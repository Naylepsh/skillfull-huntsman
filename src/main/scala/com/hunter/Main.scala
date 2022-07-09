package com.hunter

import cats.syntax.all._
import cats.effect._
import scraper.JustJoinIt.JustJoinItScraper
import domain.ExperienceLevel
import database.{createTransactor, save}
import com.hunter.domain.Requirement
import doobie.implicits._

object Main extends IOApp {

  // def run(args: List[String]): IO[ExitCode] = {
  //   val offers = JustJoinItScraper.getOffers("Scala")(ExperienceLevel.Junior)
  //   offers.flatMap(IO.println).as(ExitCode.Success)
  // }

  def run(args: List[String]): IO[ExitCode] = {
    val transactor = createTransactor(
      pathToDatabase = "database.sql",
      username = "",
      password = ""
    )
    val requirement = Requirement(name = "Scala", level = 1)
    save(requirement).transact(transactor).as(ExitCode.Success)
  }
}
