package com.hunter

import com.hunter.domain.{ExperienceLevel, Offer}
import cats.effect.IO

package object scraper {
  trait Scraper {
    def getOffers(language: String)(
        experienceLevel: ExperienceLevel
    ): IO[List[Offer]]
  }
}
