package com.cannondev.business

import cats.effect.{ExitCode, IO, Resource}
import cats.effect.implicits.*
import com.cannondev.business.algebra.{EventAlgebra, ProfileAlgebra}
import com.cannondev.business.config.DbConfig.{AppConfig, DatabaseConfig}
import com.cannondev.business.routes.Routes
import com.cannondev.business.storage.DatabaseConnection
import com.cannondev.business.storage.daos.event.EventRepository
import com.cannondev.business.storage.daos.profile.ProfileRepository
import com.cannondev.business.util.AuthClient
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import org.slf4j.LoggerFactory
import skunk.Session

class AuthService(val cfg: AppConfig)(using session: Resource[IO, Session[IO]]):

  private def logger = LoggerFactory.getLogger(this.getClass)

  given AuthClient[IO] = AuthClient[IO]
  given ProfileRepository[IO] = ProfileRepository[IO]
  given EventRepository[IO] = EventRepository[IO]
  given EventAlgebra[IO] = EventAlgebra[IO]
  given ProfileAlgebra[IO] = ProfileAlgebra[IO]

  private val apis = Router(
    "/api" -> Routes[IO]
  ).orNotFound

  private def httpServer =
    BlazeServerBuilder[IO]
      .bindHttp(8081, "0.0.0.0")
      .withHttpApp(apis)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)

  def start(): IO[ExitCode] = {
    logger.info(s"Private key: ${cfg.publicKey}")
    for httpS <- httpServer
    yield httpS
  }
