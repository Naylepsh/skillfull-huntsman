package com.hunter.scraper.JustJoinIt

import com.hunter.domain.ExperienceLevel

case class OfferSummary(
    id: String,
    title: String,
    experience_level: String,
    skills: List[Skill]
) {
  def matchesSkills(
      language: String,
      experienceLevel: ExperienceLevel
  ): Boolean =
    matchesExperienceLevel(experienceLevel) && matchesLanguage(language)

  def matchesExperienceLevel(level: ExperienceLevel): Boolean =
    OfferSummary.parseExperienceLevel(experience_level) match {
      case Right(`level`) => true
      case _              => false
    }

  def matchesLanguage(language: String): Boolean =
    skills.exists(_.name == language)
}

object OfferSummary {
  private def parseExperienceLevel(
      text: String
  ): Either[String, ExperienceLevel] = text match {
    case "senior" => Right(ExperienceLevel.Senior)
    case "mid"    => Right(ExperienceLevel.Mid)
    case "junior" => Right(ExperienceLevel.Junior)
    case other    => Left(s"Unhandled experience level: $other")
  }
}
