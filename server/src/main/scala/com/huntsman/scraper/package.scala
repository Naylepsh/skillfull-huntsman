package com.huntsman

import com.huntsman.domain.{ExperienceLevel, Offer}
import cats.effect._
import cats.syntax.all._
import database.{createTransactor, save}
import doobie.implicits._
import doobie.util.transactor.Transactor

package object scraper {
  trait Scraper {
    def getOffers(language: String)(
        experienceLevel: ExperienceLevel
    ): IO[List[Offer]]
  }

  def scrapeOffers(
      scraper: Scraper,
      transactor: Transactor[IO],
      language: String,
      experienceLevel: ExperienceLevel
  ): IO[Unit] = {
    for {
      offers <- scraper.getOffers(language)(experienceLevel)
      dbResults <- offers
        .map(offer => save(offer).transact(transactor))
        .sequence
      _ <- IO.println(s"Saved ${dbResults.length} items.")
    } yield ()
  }
}
