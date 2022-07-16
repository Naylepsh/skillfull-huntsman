package com.hunter.entrypoints

import cats.syntax.all._
import cats.effect._
import com.comcast.ip4s._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.ember.server._
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.EntityDecoder
import com.hunter.scraper.scrapeOffers
import doobie.util.transactor.Transactor
import com.hunter.domain.ExperienceLevel
import org.http4s.Request
import com.hunter.scraper.Scraper

object ScrapeService {
  def init(transactor: Transactor[IO], scrapers: List[Scraper]) = HttpRoutes
    .of[IO] { case req @ POST -> Root / "scrape" =>
      startScraping(transactor, scrapers, req)
    }

  case class ScrapeBody(skillName: String, experienceLevel: String)
  given decoder: EntityDecoder[IO, ScrapeBody] = jsonOf[IO, ScrapeBody]

  private def startScraping(
      transactor: Transactor[IO],
      scrapers: List[Scraper],
      req: Request[IO]
  ) = {
    req
      .as[ScrapeBody]
      .flatMap(body => {
        parseExperienceLevel(body.experienceLevel) match {
          case Left(reason) => BadRequest(reason)
          case Right(experienceLevel) =>
            startScrapers(transactor, scrapers, body.skillName, experienceLevel)
              .flatMap(_ => Ok("Scraping started successfully"))
        }
      })
  }

  private def parseExperienceLevel(
      level: String
  ): Either[String, ExperienceLevel] =
    level.toLowerCase match {
      case "junior" => Right(ExperienceLevel.Junior)
      case "mid"    => Right(ExperienceLevel.Mid)
      case "senior" => Right(ExperienceLevel.Senior)
      case other    => Left(s"Unknown skill level: $other")
    }

  private def startScrapers(
      transactor: Transactor[IO],
      scrapers: List[Scraper],
      skillName: String,
      experienceLevel: ExperienceLevel
  ) = {
    scrapers
      .map(scraper =>
        scrapeOffers(
          scraper,
          transactor,
          skillName,
          experienceLevel
        )
      )
      .sequence
      .start
  }

}
