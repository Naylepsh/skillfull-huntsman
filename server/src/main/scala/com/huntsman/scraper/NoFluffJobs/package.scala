package com.huntsman.scraper

import com.huntsman.domain
import com.huntsman.domain.ExperienceLevel
import com.huntsman.domain.Skill
import sttp.client3._
import cats._
import cats.implicits._
import cats.effect.IO
import org.jsoup.Jsoup
import scala.util.Try
import collection.JavaConverters._
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import cats.data.EitherT
import sttp.model.Uri
import org.jsoup.nodes.Document

package object NoFluffJobs {
  object NoFluffJobsScraper extends Scraper {

    override def getOffers(language: String)(
        experienceLevel: ExperienceLevel
    ): IO[List[domain.Offer]] = {
      val urlsT = EitherT[IO, String, List[String]](
        getOfferUrls(language, experienceLevel)
      )
      urlsT
        .flatMap(urls => {
          val result = getOffersDetails(urls, experienceLevel)
          EitherT[IO, String, List[domain.Offer]](result)
        })
        .value
        .map(_ match {
          case Right(offers) => offers

          case Left(reason) => {
            println(reason)
            List.empty
          }
        })
    }

    private def getOffersDetails(
        urls: List[String],
        experienceLevel: ExperienceLevel
    ): IO[Either[String, List[domain.Offer]]] = {
      AsyncHttpClientCatsBackend[IO]().flatMap(backend => {
        urls
          .map(url => {
            println(s"Requesting $url")

            basicRequest
              .get(uri"$url")
              .send(backend)
              .map(_.body.flatMap(body => {
                println(s"Parsing body of $url")

                parseOfferHTML(body).map(offer =>
                  Offer.toDomainOffer(offer, url, experienceLevel)
                )
              }))
          })
          .sequence
          .map(_.sequence)
      })
    }

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
          doc
            .select(".posting-list-item")
            .asScala
            .map(elem => s"""https://nofluffjobs.com${elem.attr("href")}""")
            .toList

        val nextPage = doc
          .select(".page-item.active + .page-item")
          .asScala
          .headOption

        OfferListResult(detailedOfferUrls, nextPage.isDefined)
      }.toEither.left.map(_.toString)

    def getOfferListHTML(skill: String, experienceLevel: ExperienceLevel)(
        page: Int = 1
    ): IO[Either[String, String]] = sendGetRequest(
      uri"https://nofluffjobs.com/pl/${skill}?criteria=seniority%3D${experienceLevel.show}&page=${page}"
    )

    private def sendGetRequest(uri: Uri): IO[Either[String, String]] = {
      println(s"Requesting $uri")

      AsyncHttpClientCatsBackend[IO]().flatMap(backend => {
        val request = basicRequest.get(uri)

        request.send(backend).map(_.body)
      })
    }

    def parseOfferHTML(html: String): Either[String, Offer] = Try {
      val doc = Jsoup.parse(html)

      Offer(
        title = getOfferTitle(doc),
        generalDescription = getOfferGeneralDescription(doc),
        tasksDescription = getOfferTasksDescription(doc),
        skills = getOfferSkills(doc)
      )
    }.toEither.left.map(_.toString)

    private def getOfferTitle(doc: Document): String =
      doc.select(".posting-details-description h1").asScala.head.attr("text")

    private def getOfferGeneralDescription(doc: Document): Option[String] = doc
      .select("#posting-description")
      .asScala
      .headOption
      .map(_.attr("text"))

    private def getOfferTasksDescription(doc: Document): Option[String] = doc
      .select("posting-tasks")
      .asScala
      .headOption
      .map(_.attr("text"))

    private def getOfferSkills(doc: Document): List[Skill] =
      getOfferRequiredSkills(doc) ::: getOfferNiceToHaveSkills(doc)

    private def getOfferRequiredSkills(doc: Document): List[Skill] = {
      val skillLevel = doc.select("#posting-seniority svg").asScala.length - 1

      doc
        .select("common-posting-requirements")
        .next
        .select("common-posting-item-tag")
        .asScala
        .map(elem => Skill(elem.attr("text"), skillLevel))
        .toList
    }

    private def getOfferNiceToHaveSkills(doc: Document): List[Skill] = doc
      .select("#posting-nice-to-have common-posting-item-tag")
      .asScala
      .map(elem => Skill(elem.attr("text"), 1))
      .toList
  }

  case class OfferListResult(urls: List[String], hasMore: Boolean)

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
              case Right(OfferListResult(newUrls, false)) => {
                IO(Right(newUrls ::: urls))
              }

              case Right(OfferListResult(newUrls, true)) => {
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
