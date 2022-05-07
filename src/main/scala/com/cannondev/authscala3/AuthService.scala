package com.cannondev.authscala3

import cats.effect.{ExitCode, IO}
import com.cannondev.authscala3.config.AppConfig
import com.cannondev.authscala3.config.DbConfig.DatabaseConfig
import com.cannondev.authscala3.storage.DatabaseConnection
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router

class AuthService(cfg: DatabaseConfig) {

  private val apis = Router(
    "/api" -> Authscala3Routes.registerRoute[IO]
  ).orNotFound

  private def httpServer =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(apis)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)

  def start(): IO[ExitCode] = {
    for {
      httpS <- httpServer
    } yield httpS
  }
}
