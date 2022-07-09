package com.hunter

import cats._
import cats.implicits._
import cats.effect.IO
import doobie._
import doobie.implicits._
import com.hunter.domain
import com.hunter.database.models.Requirement

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

  def save(requirement: domain.Requirement): ConnectionIO[Unit] = {
    sql"INSERT OR IGNORE INTO requirements(name) VALUES (${requirement.name})".update.run.void
  }
}
