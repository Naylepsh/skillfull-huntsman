package com.hunter.entrypoints

import doobie.util.transactor.Transactor
import cats.syntax.all._
import cats.effect._
import com.comcast.ip4s._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.ember.server._

object RelatedSkillsService {
  def init(transactor: Transactor[IO]) = HttpRoutes.of[IO] {
    case req @ GET -> Root / "skills" / skill / level =>
      Ok(s"$skill / $level")
  }
}
