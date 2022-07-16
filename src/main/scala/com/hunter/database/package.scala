package com.hunter

import cats._
import cats.implicits._
import cats.effect.IO
import doobie._
import doobie.implicits._
import com.hunter.domain
import com.hunter.domain.ExperienceLevel

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

    val saveSkills = offer.skills.map(save).sequence

    for {
      _ <- saveOffer
      _ <- saveSkills
      _ <- offer.skills
        .map(save(offer.url))
        .sequence
    } yield ()
  }

  private def save(
      offerUrl: String
  )(skill: domain.Skill): ConnectionIO[Unit] = {
    sql"""INSERT INTO offer_skills(offer_url, skill_name, level) 
         |VALUES ($offerUrl, ${skill.name}, ${skill.level})""".stripMargin.update.run.void
  }

  private def save(skill: domain.Skill): ConnectionIO[Unit] = {
    sql"INSERT OR IGNORE INTO skills(name) VALUES (${skill.name})".update.run.void
  }

  given showExperienceLevel: Show[ExperienceLevel] =
    Show.show(experienceLevel => {
      experienceLevel match {
        case ExperienceLevel.Junior => "junior"
        case ExperienceLevel.Mid    => "mid"
        case ExperienceLevel.Senior => "senior"
      }
    })
}
