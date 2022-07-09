package com.hunter.scraper.JustJoinIt

import com.hunter.domain.Requirement

case class Skill(name: String, level: Int)

object Skill {
  def toRequirement(skill: Skill): Requirement =
    Requirement(name = skill.name, level = skill.level)
}
