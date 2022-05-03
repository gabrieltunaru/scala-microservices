package com.cannondev.authscala3

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.Literals.ipv4
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router

object Main extends IOApp:
  def run(args: List[String]): IO[ExitCode] = {
    import scala.concurrent.ExecutionContext.global
    val apis = Router(
      "/api" -> Authscala3Routes.registerRoute[IO],
    ).orNotFound

    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(apis)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

