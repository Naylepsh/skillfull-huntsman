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

  case class RelatedSkill(name: String, count: Int)

  def getRelatedSkills(
      skillName: String,
      experienceLevel: ExperienceLevel
  ): ConnectionIO[List[RelatedSkill]] = {
    sql"""
      | SELECT related_skills.name as name, COUNT(*) as count
      | FROM skills 
      | JOIN offer_skills on offer_skills.skill_name = skills.name
      | JOIN offers on offers.url = offer_skills.offer_url
      | JOIN offer_skills related_oss on related_oss.offer_url = offer_skills.offer_url
      | JOIN skills related_skills on related_skills.name = related_oss.skill_name
      | WHERE skills.name = ${skillName} COLLATE NOCASE
      |   AND offers.experience_level = ${experienceLevel.show} COLLATE NOCASE
      |   AND related_skills.name != ${skillName} COLLATE NOCASE
      | GROUP BY related_skills.name
      | ORDER BY count desc
      """.stripMargin.query[RelatedSkill].to[List]
  }

  def save(offer: domain.Offer): ConnectionIO[Unit] = {
    val saveOffer =
      sql"""
      | INSERT OR IGNORE INTO offers(url, title, description, experience_level)
      | VALUES (${offer.url}, ${offer.title}, ${offer.description}, ${offer.experienceLevel.show})
      |""".stripMargin.update.run.void

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
    sql"""
    | INSERT INTO offer_skills(offer_url, skill_name, level) 
    | VALUES ($offerUrl, ${skill.name}, ${skill.level})
    |""".stripMargin.update.run.void
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
