package com.cannondev.authscala3

import cats.effect.{ExitCode, IO, Resource}
import com.cannondev.authscala3.config.DbConfig.{AppConfig, DatabaseConfig}
import com.cannondev.authscala3.storage.DatabaseConnection
import com.cannondev.authscala3.algebra.UserAlgebra
import com.cannondev.authscala3.routes.Routes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import org.slf4j.LoggerFactory
import skunk.Session

class AuthService(cfg: AppConfig)(using session: Resource[IO, Session[IO]]):

  private def logger = LoggerFactory.getLogger(this.getClass)

  given AppConfig = cfg

  given UserAlgebra[IO] = UserAlgebra[IO]
  private val routes = Routes[IO]

  private val apis = Router(
    "/api" -> routes.registerRoute(cfg)
  ).orNotFound

  private def httpServer =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(apis)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)

  def start(): IO[ExitCode] = {
    logger.info(s"Private key: ${cfg.hasingPrivateKey}")
    for httpS <- httpServer
    yield httpS
  }
