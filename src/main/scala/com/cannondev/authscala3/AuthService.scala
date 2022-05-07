package com.cannondev.authscala3

import cats.effect.{ExitCode, IO, Resource}
import com.cannondev.authscala3.config.DbConfig.{AppConfig, DatabaseConfig}
import com.cannondev.authscala3.storage.DatabaseConnection
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import org.slf4j.LoggerFactory
import skunk.Session

class AuthService(val cfg: AppConfig)(implicit val session: Resource[IO, Session[IO]]) {

  private def logger = LoggerFactory.getLogger(this.getClass)

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
    logger.info(s"Private key: ${cfg.hasingPrivateKey}")
    for {
      httpS <- httpServer
    } yield httpS
  }
}
