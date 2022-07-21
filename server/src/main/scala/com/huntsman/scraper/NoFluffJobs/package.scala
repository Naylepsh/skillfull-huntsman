package com.huntsman.scraper

import com.huntsman.domain.ExperienceLevel
import com.huntsman.domain.Offer
import sttp.client3._
import cats._
import cats.implicits._
import cats.effect.IO
import org.jsoup.Jsoup
import scala.util.Try
import collection.JavaConverters._
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import cats.data.EitherT

package object NoFluffJobs {
  object NoFluffJobsScraper extends Scraper {

    override def getOffers(language: String)(
        experienceLevel: ExperienceLevel
    ): IO[List[Offer]] = ???

    def getOfferList(
        language: String,
        experienceLevel: ExperienceLevel,
        page: Int = 1,
        urls: List[String] = List()
    ): IO[Either[String, List[String]]] = {
      getOfferListHTML(language, experienceLevel).flatMap(_ match {
        case Left(reason) => IO(Left(reason))

        case Right(html) => {
          parseOfferListHTML(html) match {
            case Right(OfferListResult(newUrls, None)) => {
              IO(Right(newUrls ::: urls))
            }

            case Right(OfferListResult(newUrls, Some(nextPageUrl))) => {
              getOfferList(language, experienceLevel, page + 1)
            }

            case Left(reason) => IO(Left(reason.toString))
          }
        }
      })
    }

    case class OfferListResult(urls: List[String], nextPageUrl: Option[String])

    def parseOfferListHTML(html: String): Either[Throwable, OfferListResult] =
      Try {
        val doc = Jsoup.parse(html)
        val detailedOfferUrls =
          doc.select(".posting-list-item").asScala.map(_.attr("href")).toList

        val nextPageUrl = doc
          .select(".page-item.active + .page-item")
          .asScala
          .headOption
          .map(_.attr("href"))

        OfferListResult(detailedOfferUrls, nextPageUrl)
      }.toEither

    def getOfferListHTML(
        skill: String,
        experienceLevel: ExperienceLevel,
        page: Int = 1
    ): IO[Either[String, String]] = {
      AsyncHttpClientCatsBackend[IO]().flatMap(backend => {
        val request = basicRequest.get(
          uri"https://nofluffjobs.com/pl/${skill}?criteria=seniority%3D${experienceLevel.show}&page=${page}"
        )

        request.send(backend).map(_.body)
      })
    }
  }

  given showExperienceLevel: Show[ExperienceLevel] = new Show[ExperienceLevel] {
    def show(experienceLevel: ExperienceLevel): String = experienceLevel match {
      case ExperienceLevel.Junior => "junior"
      case ExperienceLevel.Mid    => "mid"
      case ExperienceLevel.Senior => "senior"
    }
  }
}
