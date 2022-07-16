package com.hunter.scraper.JustJoinIt

import com.hunter.domain

case class Skill(name: String, level: Int)

object Skill {
  def toDomainModel(skill: Skill): domain.Skill =
    domain.Skill(name = skill.name, level = skill.level)
}
