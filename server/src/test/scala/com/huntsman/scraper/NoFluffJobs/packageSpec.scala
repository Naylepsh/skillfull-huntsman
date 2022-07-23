package com.hunter.scraper.NoFluffJobs

import cats.effect._
import cats.effect.unsafe.implicits.global
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.EitherValues.convertEitherToValuable
import com.huntsman.scraper.NoFluffJobs._
import com.huntsman.domain.ExperienceLevel

class packageSpec extends AnyFlatSpec with Matchers {
  "parseOfferListHTML" should "return a bunch of offer urls for a popular technology" in {
    val htmlE = NoFluffJobsScraper
      .getOfferListHTML("Python", ExperienceLevel.Mid)(page = 1)
      .unsafeRunSync()

    htmlE.isRight shouldBe true

    htmlE.foreach(html => {
      val result = NoFluffJobsScraper.parseOfferListHTML(html)
      result.value.urls.length should be > 0
      result.value.hasMore shouldBe true
    })
  }

  "getOfferUrls" should "go through all entire pagination and collect urls" in {
    import packageSpec._

    val urlsToDiscover =
      htmlToFakeOfferList.values.map(_.urls)
    val urlsE = getOfferUrls(config, "Scala", ExperienceLevel.Junior)(1, List())
      .unsafeRunSync()

    urlsE.value.length shouldBe urlsToDiscover.foldLeft(0)(_ + _.length)
  }
}

object packageSpec {
  val pageToFakeHTML = Map[Int, String](1 -> "1", 2 -> "2", 3 -> "3")
  def fakeServeHTML(page: Int): IO[Either[String, String]] = IO {
    pageToFakeHTML.get(page) match {
      case Some(html) => Right(html)
      case None       => Left("")
    }
  }

  val htmlToFakeOfferList = Map[String, OfferListResult](
    "1" -> OfferListResult(List("https://url.to/offer/1"), true),
    "2" -> OfferListResult(List("https://url.to/offer/2"), true),
    "3" -> OfferListResult(List("https://url.to/offer/3"), false)
  )
  def fakeParseHTML(html: String): Either[String, OfferListResult] = {
    htmlToFakeOfferList.get(html) match {
      case Some(data) => Right(data)
      case None       => Left("")
    }
  }

  val config = GetOfferListConfig(fakeServeHTML, fakeParseHTML)
}
