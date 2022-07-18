package com.huntsman.scraper

import cats.implicits._
import cats.effect._
import cats.effect.implicits._
import sttp.client3._
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._
import com.huntsman.domain.ExperienceLevel
import com.huntsman.domain.Offer
import cats.data.OptionT
import sttp.model.Uri

package object JustJoinIt {
  object JustJoinItScraper extends Scraper {
    def getOffers(skill: String)(
        experienceLevel: ExperienceLevel
    ): IO[List[Offer]] = {
      println(
        s"Getting offers for skill: $skill and exp. level: $experienceLevel"
      )

      getAllOffers().flatMap(_ match {
        case Right(offers) =>
          getDetailsOfMatchingOffers(skill, experienceLevel)(offers)
        case Left(reason) => {
          println(reason)
          IO(List.empty)
        }
      })
    }

    private def getDetailsOfMatchingOffers(
        skill: String,
        experienceLevel: ExperienceLevel
    )(offers: List[OfferSummary]) = {
      val detailedOffers = offers
        .mapFilter[IO[Either[String, (OfferDetailed, Uri)]]] {
          case offer @ OfferSummary(id, _, _, _)
              if offer.matchesSkills(skill, experienceLevel) =>
            getOfferDetails(id).some
          case _ => None
        }
        .sequence

      detailedOffers.map(_.mapFilter[Offer] {
        case Right((offer, url)) =>
          OfferDetailed.toOffer(offer, experienceLevel, url.toString).some
        case Left(reason) => {
          println(reason)
          None
        }
      })
    }

    private def getAllOffers(): IO[Either[String, List[OfferSummary]]] = IO {
      val backend = HttpClientSyncBackend()
      val request = basicRequest.get(uri"https://justjoin.it/api/offers")

      val response = request.send(backend)
      response.body.flatMap(decode[List[OfferSummary]]).left.map(_.toString)
    }

    private def getOfferDetails(
        id: String
    ): IO[Either[String, (OfferDetailed, Uri)]] =
      IO {
        println(s"Getting an offer for $id...")

        val backend = HttpClientSyncBackend()
        val url = uri"https://justjoin.it/api/offers/$id"
        val request = basicRequest.get(url)

        val response = request.send(backend)
        response.body
          .flatMap(decode[OfferDetailed])
          .map(offer => (offer, url))
          .left
          .map(_.toString)
      }
  }
}
