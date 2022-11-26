package com.cannondev.authscala3

import cats.effect.{ExitCode, IO, IOApp}
import com.cannondev.authscala3.config.DbConfig
import com.cannondev.authscala3.config.DbConfig.DatabaseConfig
import com.cannondev.authscala3.storage.{DBMigration, DatabaseConnection}
import com.comcast.ip4s.Literals.ipv4
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import cats.effect.*
import cats.effect.implicits.*
import skunk.implicits.*
import skunk.Session
object Main extends IOApp:

  val myApp: IO[Unit] = for
    cfg <- DbConfig.appConfig.load[IO]

    given Resource[IO, Session[IO]] =  DatabaseConnection.getSession(cfg.dbConfig)
    _ <- DatabaseConnection.run
    _ <- DBMigration.migrate[IO](cfg.dbConfig)

    service = AuthService(cfg)
    httpServer <- IO.pure(service.start())
    _ <- httpServer
  yield ()

  def run(args: List[String]): IO[ExitCode] =
    myApp.as(ExitCode.Success)

