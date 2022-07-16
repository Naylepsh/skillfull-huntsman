package com.hunter

package object domain {

  enum ExperienceLevel:
    case Junior, Mid, Senior

  case class Skill(
      name: String,
      level: Int
  )

  case class Offer(
      url: String,
      title: String,
      description: String,
      experienceLevel: ExperienceLevel,
      skills: List[Skill]
  )

}
