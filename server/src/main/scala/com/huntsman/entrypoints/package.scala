package com.huntsman

import com.huntsman.domain.ExperienceLevel

package object entrypoints {
  def parseExperienceLevel(
      level: String
  ): Either[String, ExperienceLevel] =
    level.toLowerCase match {
      case "junior" => Right(ExperienceLevel.Junior)
      case "mid"    => Right(ExperienceLevel.Mid)
      case "senior" => Right(ExperienceLevel.Senior)
      case other    => Left(s"Unknown skill level: $other")
    }
}
