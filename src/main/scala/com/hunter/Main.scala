package com.hunter

import cats.syntax.all._
import cats.effect._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    IO.println(args).as(ExitCode.Success)
}
