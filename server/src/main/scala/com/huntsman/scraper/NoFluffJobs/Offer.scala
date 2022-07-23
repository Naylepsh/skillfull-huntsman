package com.huntsman.scraper.NoFluffJobs

import com.huntsman.domain
import com.huntsman.domain._

case class Offer(
    title: String,
    generalDescription: Option[String],
    tasksDescription: Option[String],
    skills: List[Skill]
)

object Offer {
  def toDomainOffer(
      offer: Offer,
      url: String,
      experienceLevel: ExperienceLevel
  ): domain.Offer = {
    domain.Offer(
      url = url,
      title = offer.title,
      description = s"""
      | ${offer.generalDescription.getOrElse("")}
      | ${offer.tasksDescription.getOrElse("")}
      """.stripMargin,
      experienceLevel = experienceLevel,
      skills = offer.skills
    )
  }
}
