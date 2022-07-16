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
import com.hunter.scraper.JustJoinIt.JustJoinItScraper
import com.hunter.domain.ExperienceLevel
import org.http4s.Request

object ScrapeService {
  def init(transactor: Transactor[IO]) = HttpRoutes
    .of[IO] { case req @ POST -> Root / "scrape" =>
      scrape(transactor, req)
    }
    .orNotFound

  case class ScrapeBody(skillName: String, experienceLevel: String)
  given decoder: EntityDecoder[IO, ScrapeBody] = jsonOf[IO, ScrapeBody]

  private def scrape(transactor: Transactor[IO], req: Request[IO]) = {
    req
      .as[ScrapeBody]
      .flatMap(body => {
        parseExperienceLevel(body.experienceLevel) match {
          case Left(reason) => BadRequest(reason)
          case Right(experienceLevel) =>
            scrapeOffers(
              JustJoinItScraper,
              transactor,
              body.skillName,
              experienceLevel
            ).start.flatMap(_ => Ok("Scraping request successfully sent"))
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

}
