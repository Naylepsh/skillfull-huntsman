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

package object NoFluffJobs {
  object NoFluffJobsScraper extends Scraper {

    override def getOffers(language: String)(
        experienceLevel: ExperienceLevel
    ): IO[List[Offer]] = ???

    def getOfferUrls(
        language: String,
        experienceLevel: ExperienceLevel
    ): IO[Either[String, List[String]]] = {
      val initialPage = 1
      val initialUrls = List[String]()

      NoFluffJobs.getOfferUrls(
        config = GetOfferListConfig(
          getHTML = getOfferListHTML(language, experienceLevel) _,
          parseHTML = parseOfferListHTML _
        ),
        language,
        experienceLevel
      )(initialPage, initialUrls)
    }

    def parseOfferListHTML(html: String): Either[String, OfferListResult] =
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
      }.toEither.left.map(_.toString)

    def getOfferListHTML(skill: String, experienceLevel: ExperienceLevel)(
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

  case class OfferListResult(urls: List[String], nextPageUrl: Option[String])

  case class GetOfferListConfig(
      getHTML: Int => IO[Either[String, String]],
      parseHTML: String => Either[String, OfferListResult]
  )

  def getOfferUrls(
      config: GetOfferListConfig,
      language: String,
      experienceLevel: ExperienceLevel
  ) = {
    def crawl(
        page: Int,
        urls: List[String]
    ): IO[Either[String, List[String]]] = {
      config
        .getHTML(page)
        .flatMap(_ match {
          case Left(reason) => IO(Left(reason))

          case Right(html) => {
            config.parseHTML(html) match {
              case Right(OfferListResult(newUrls, None)) => {
                IO(Right(newUrls ::: urls))
              }

              case Right(OfferListResult(newUrls, Some(nextPageUrl))) => {
                crawl(page + 1, newUrls ::: urls)
              }

              case Left(reason) => IO(Left(reason))
            }
          }
        })
    }

    crawl
  }

  given showExperienceLevel: Show[ExperienceLevel] = new Show[ExperienceLevel] {
    def show(experienceLevel: ExperienceLevel): String = experienceLevel match {
      case ExperienceLevel.Junior => "junior"
      case ExperienceLevel.Mid    => "mid"
      case ExperienceLevel.Senior => "senior"
    }
  }
}
