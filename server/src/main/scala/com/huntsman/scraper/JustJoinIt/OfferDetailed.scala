package com.huntsman.scraper.JustJoinIt

import com.huntsman.domain.ExperienceLevel
import com.huntsman.domain.Offer

case class OfferDetailed(
    body: String,
    title: String,
    skills: List[Skill],
    experience_level: String
)

object OfferDetailed {
  def toOffer(
      offer: OfferDetailed,
      experienceLevel: ExperienceLevel,
      url: String
  ): Offer =
    Offer(
      title = offer.title,
      description = offer.body,
      experienceLevel = experienceLevel,
      url = url,
      skills = offer.skills.map(Skill.toDomainModel)
    )
}
