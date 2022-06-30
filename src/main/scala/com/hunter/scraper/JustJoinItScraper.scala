package com.hunter.scraper

import cats.implicits._
import cats.effect._
import cats.effect.implicits._
import sttp.client3._
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._
import com.hunter.domain.{ExperienceLevel, Offer}
import sttp.model.Uri
import com.hunter.domain.Requirement

object JustJoinItScraper extends Scraper {
  case class Skill(name: String, level: Int)

  // TODO: transform to TypeClass?
  object Skill {
    def toRequirement(skill: Skill): Requirement =
      Requirement(name = skill.name, level = skill.level)
  }

  case class OfferSummary(
      title: String,
      experience_level: String,
      skills: List[Skill]
  )

  case class OfferDetailed(
      body: String,
      title: String,
      skills: List[Skill],
      experience_level: String
  )

  // TODO: transform to TypeClass?
  object OfferDetailed {
    def toOffer(offer: OfferDetailed, experienceLevel: ExperienceLevel): Offer =
      Offer(
        title = offer.title,
        description = offer.body,
        experienceLevel = experienceLevel,
        requirements = offer.skills.map(Skill.toRequirement)
      )
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

  private def parseExperienceLevel(
      text: String
  ): Either[String, ExperienceLevel] = text match {
    case "senior" => Right(ExperienceLevel.Senior)
    case "mid"    => Right(ExperienceLevel.Mid)
    case "junior" => Right(ExperienceLevel.Junior)
    case other    => Left(s"Unhandled experience level: $other")
  }

  private def matchesRequirements(
      language: String,
      experienceLevel: ExperienceLevel
  )(offer: OfferSummary): Boolean = {
    val matchesExperienceLevel = parseExperienceLevel(offer.experience_level)
      .map(_ == experienceLevel)
      .isRight
    val matchesLanguage = offer.skills.exists(_.name == language)

    return matchesExperienceLevel && matchesLanguage
  }

  private def getOffer(title: String): IO[Option[OfferDetailed]] = IO {
    val backend = HttpClientSyncBackend()
    val request = basicRequest.get(uri"https://justjoin.it/api/offers/$title")

    val response = request.send(backend)
    response.body.flatMap(decode[OfferDetailed]) match {
      case Left(_)      => None
      case Right(offer) => Some(offer)
    }
  }

  def getOffers(language: String)(
      experienceLevel: ExperienceLevel
  ): IO[List[Offer]] = {
    def getDetailedOffers(offers: List[OfferSummary]) = offers
      .mapFilter[IO[Option[OfferDetailed]]] {
        case offer if matchesRequirements(language, experienceLevel)(offer) =>
          getOffer(offer.title).some
      }
      .sequence

    getAllOffers()
      .flatMap(getDetailedOffers)
      .map(_.mapFilter[Offer] {
        case Some(offer) => OfferDetailed.toOffer(offer, experienceLevel).some
        case None        => None
      })

  }
}
