package com.cannondev.authscala3

import cats.effect.{ExitCode, IO, IOApp}
import com.cannondev.authscala3.config.DbConfig
import com.cannondev.authscala3.config.DbConfig.DatabaseConfig
import com.comcast.ip4s.Literals.ipv4
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router

object Main extends IOApp:

  val myApp: IO[Unit] = for {
    cfg <- DbConfig.appConfig.load[IO]
    
    service = new AuthService(cfg)
    httpServer <- IO.pure(service.start())
    _ <- httpServer
  } yield ()

  def run(args: List[String]): IO[ExitCode] = {
    myApp.as(ExitCode.Success)
  }

