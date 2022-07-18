package com.huntsman.scraper.JustJoinIt

import com.huntsman.domain

case class Skill(name: String, level: Int)

object Skill {
  def toDomainModel(skill: Skill): domain.Skill =
    domain.Skill(name = skill.name, level = skill.level)
}
