package com.hunter

package object domain {

  enum ExperienceLevel:
    case Junior, Mid, Senior

  case class Requirement(
      name: String,
      level: Int
  )

  case class Offer(
      title: String,
      description: String,
      experienceLevel: ExperienceLevel,
      requirements: List[Requirement]
  )

}
