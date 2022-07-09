package com.hunter

import cats._
import cats.implicits._
import cats.effect.IO
import doobie._
import doobie.implicits._
import com.hunter.domain
import com.hunter.domain.ExperienceLevel
import java.util.UUID

package object database {
  def createTransactor(
      pathToDatabase: String,
      username: String,
      password: String
  ): Transactor[IO] = {
    Transactor.fromDriverManager[IO](
      "org.sqlite.JDBC",
      s"jdbc:sqlite:$pathToDatabase",
      username,
      password
    )
  }

  def save(offer: domain.Offer): ConnectionIO[Unit] = {
    val saveOffer =
      sql"""INSERT OR IGNORE INTO offers(url, title, description, experience_level)
            |VALUES (${offer.url}, ${offer.title}, ${offer.description}, ${offer.experienceLevel.show})""".stripMargin.update.run.void

    val saveRequirements = offer.requirements.map(save).sequence

    for {
      _ <- saveOffer
      _ <- saveRequirements
      _ <- offer.requirements
        .map(save(offer.url))
        .sequence
    } yield ()
  }

  private def save(
      offerUrl: String
  )(requirement: domain.Requirement): ConnectionIO[Unit] = {
    sql"""INSERT INTO offer_requirements(offer_url, requirement_name, level) 
         |VALUES ($offerUrl, ${requirement.name}, ${requirement.level})""".stripMargin.update.run.void
  }

  private def save(requirement: domain.Requirement): ConnectionIO[Unit] = {
    sql"INSERT OR IGNORE INTO requirements(name) VALUES (${requirement.name})".update.run.void
  }

  given showExperienceLevel: Show[ExperienceLevel] =
    Show.show(experienceLevel => {
      experienceLevel match {
        case ExperienceLevel.Junior => "junior"
        case ExperienceLevel.Mid    => "mid"
        case ExperienceLevel.Senior => "senior"
      }
    })

  given uuidMeta: Meta[UUID] =
    Meta[String].imap[UUID](UUID.fromString)(_.toString)
}
