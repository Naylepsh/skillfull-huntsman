package com.hunter.scraper.JustJoinIt

import cats.effect._
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import com.hunter.domain.ExperienceLevel

class OfferSummarySpec extends AnyFlatSpec with Matchers {
  import OfferSummarySpec._

  "matchesSkills" should "return true for an offer with given experience level AND language" in {
    juniorScalaDevOffer.matchesSkills(
      "Scala",
      ExperienceLevel.Junior
    ) shouldBe true
  }

  it should "return false when an offer doesn't match given experience level" in {
    juniorScalaDevOffer.matchesSkills(
      "Scala",
      ExperienceLevel.Mid
    ) shouldBe false
  }

  it should "return false when an offer doesn't match given language" in {
    juniorScalaDevOffer.matchesSkills(
      "TypeScript",
      ExperienceLevel.Junior
    ) shouldBe false
  }
}

object OfferSummarySpec {
  val juniorScalaDevOffer = OfferSummary(
    id = "offer-1",
    title = "Junior Scala Developer",
    experience_level = "junior",
    skills = List(Skill("Scala", 1))
  )
}
