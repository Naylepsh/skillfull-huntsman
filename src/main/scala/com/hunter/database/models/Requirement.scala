package com.hunter.database.models

import com.hunter.domain

case class Requirement(name: String)

object Requirement {
  def apply(requirement: domain.Requirement): Requirement =
    Requirement(name = requirement.name)
}
