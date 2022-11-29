package com.cannondev.business

import cats.effect.{ExitCode, IO, IOApp}
import com.cannondev.business.config.DbConfig
import com.cannondev.business.config.DbConfig.DatabaseConfig
import com.cannondev.business.storage.{DBMigration, DatabaseConnection}
import com.comcast.ip4s.Literals.ipv4
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import skunk.Session
import cats.effect.kernel.Resource

object Main extends IOApp:


  val myApp: IO[Unit] = for
    cfg <- DbConfig.appConfig.load[IO]

    given Resource[IO, Session[IO]] = DatabaseConnection.getSession(cfg.dbConfig)
    _ <- DatabaseConnection.run
    _ <- DBMigration.migrate[IO](cfg.dbConfig)

    service = AuthService(cfg)
    httpServer <- IO.pure(service.start())
    _ <- httpServer
  yield ()

  def run(args: List[String]): IO[ExitCode] = {
    myApp.as(ExitCode.Success)
  }

