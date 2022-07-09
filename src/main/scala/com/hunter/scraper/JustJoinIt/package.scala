package com.hunter.scraper

import cats.implicits._
import cats.effect._
import cats.effect.implicits._
import sttp.client3._
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._
import com.hunter.domain.ExperienceLevel
import com.hunter.domain.Offer

package object JustJoinIt {
  object JustJoinItScraper extends Scraper {
    def getOffers(language: String)(
        experienceLevel: ExperienceLevel
    ): IO[List[Offer]] = {
      println(
        s"Getting offers for language: $language and exp. level: $experienceLevel"
      )

      getAllOffers()
        .flatMap(getDetailedOffers(language, experienceLevel))
        .map(_.mapFilter[Offer] {
          case Some(offer) => OfferDetailed.toOffer(offer, experienceLevel).some
          case None        => None
        })
    }

    private def getAllOffers(): IO[List[OfferSummary]] = IO {
      val backend = HttpClientSyncBackend()
      val request = basicRequest.get(uri"https://justjoin.it/api/offers")

      val response = request.send(backend)
      response.body.flatMap(decode[List[OfferSummary]]) match {
        case Left(_)       => List()
        case Right(offers) => offers
      }
    }

    private def getDetailedOffers(
        language: String,
        experienceLevel: ExperienceLevel
    )(offers: List[OfferSummary]): IO[List[Option[OfferDetailed]]] = offers
      .mapFilter[IO[Option[OfferDetailed]]] {
        case offer @ OfferSummary(id, _, _, _)
            if offer.matchesRequirements(language, experienceLevel) =>
          getOffer(id).some

        case _ => None
      }
      .sequence

    private def getOffer(title: String): IO[Option[OfferDetailed]] = IO {
      println(s"Getting an offer for $title...")

      val backend = HttpClientSyncBackend()
      val request = basicRequest.get(uri"https://justjoin.it/api/offers/$title")

      val response = request.send(backend)
      response.body.flatMap(decode[OfferDetailed]) match {
        case Left(_)      => None
        case Right(offer) => Some(offer)
      }
    }
  }
}
