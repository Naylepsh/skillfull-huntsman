package com.hunter

import cats.syntax.all._
import cats.effect._
import scraper.JustJoinItScraper
import domain.ExperienceLevel
import com.hunter.domain.ExperienceLevel

object Main extends IOApp {

  // def run(args: List[String]): IO[ExitCode] =
  //   IO.println(args).as(ExitCode.Success)

  def run(args: List[String]): IO[ExitCode] = {
    val offers = JustJoinItScraper.getOffers("Scala")(ExperienceLevel.Junior)
    offers.flatMap(IO.println).as(ExitCode.Success)
  }
}
