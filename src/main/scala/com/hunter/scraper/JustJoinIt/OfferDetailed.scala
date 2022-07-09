package com.hunter.scraper.JustJoinIt

import com.hunter.domain.ExperienceLevel
import com.hunter.domain.Offer

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
      requirements = offer.skills.map(Skill.toRequirement)
    )
}
