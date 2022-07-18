package com.huntsman.entrypoints

import doobie.util.transactor.Transactor
import doobie.implicits._
import cats.syntax.all._
import cats.effect._
import com.comcast.ip4s._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.ember.server._
import com.huntsman.database.{getRelatedSkills, RelatedSkill}
import io.circe.generic.auto._
import io.circe.syntax._

object RelatedSkillsService {
  def init(transactor: Transactor[IO]) = HttpRoutes.of[IO] {
    case req @ GET -> Root / "skills" / skill / level => {
      parseExperienceLevel(level) match {
        case Left(reason) => BadRequest(reason)
        case Right(experienceLevel) => {
          getRelatedSkills(skill, experienceLevel)
            .transact(transactor)
            .flatMap(skills => Ok(skills.map(_.asJson).asJson))
        }
      }
    }
  }
}
