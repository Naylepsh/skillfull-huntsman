package com.hunter.database

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterEach
import com.hunter.domain.Offer
import com.hunter.domain.ExperienceLevel
import com.hunter.domain.Skill
import doobie._
import doobie.implicits._

class DatabaseSpec extends AnyFlatSpec with Matchers with BeforeAndAfterEach {
  import DatabaseSpec._
  import cats.effect.unsafe.implicits.global

  override protected def beforeEach(): Unit = {
    val deleteOfferSkills = sql"delete from offer_skills".update.run
    val deleteOffers = sql"delete from offers".update.run
    val deleteSkills = sql"delete from skills".update.run

    val teardown = for {
      _ <- deleteOfferSkills
      _ <- deleteOffers
      _ <- deleteSkills
    } yield ()

    teardown.transact(transactor).unsafeRunSync()
  }

  "Save" should "store an offer in the database" in {
    val offer = Offer(
      url = "http://localhost:8080/offers/1",
      title = "Some Offer",
      description = "N/A",
      experienceLevel = ExperienceLevel.Junior,
      skills = List(Skill(name = "Scala", level = 5))
    )

    save(offer).transact(transactor).unsafeRunSync()

    val numberOfOffersStored = sql"select count(*) from offers"
      .query[Int]
      .unique
      .transact(transactor)
      .unsafeRunSync()

    numberOfOffersStored shouldBe 1
  }
}

object DatabaseSpec {
  val transactor = createTransactor("database.test.sql", "", "")
}
