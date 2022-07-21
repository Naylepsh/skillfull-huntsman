package com.hunter.scraper.NoFluffJobs

import cats.effect._
import cats.effect.unsafe.implicits.global
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.EitherValues.convertEitherToValuable
import com.huntsman.scraper.NoFluffJobs.NoFluffJobsScraper
import com.huntsman.domain.ExperienceLevel

class packageSpec extends AnyFlatSpec with Matchers {
  "parseOfferListHTML" should "return a bunch of offer urls for a popular technology" in {
    val htmlE = NoFluffJobsScraper
      .getOfferListHTML("Python", ExperienceLevel.Mid)
      .unsafeRunSync()

    htmlE.isRight shouldBe true

    htmlE.foreach(html => {
      val result = NoFluffJobsScraper.parseOfferListHTML(html)
      result.value.urls.length should be > 0
      result.value.nextPageUrl.isDefined shouldBe true
    })
  }
}
