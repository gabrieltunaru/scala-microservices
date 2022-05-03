package com.cannondev.authscala3

import cats.Monad
import cats.effect.Sync
import cats.effect.kernel.Concurrent
import cats.implicits.*
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl

object Authscala3Routes:

  def registerRoute[F[_]: Concurrent]: HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl._
    implicit val decoder: EntityDecoder[F, AuthInfo.Info] = jsonOf[F, AuthInfo.Info]
    HttpRoutes.of[F] {
      case req @ POST -> Root / "register"  =>
        for {
          user <- req.as[AuthInfo.Info]
          res <- Ok(s"Registered user ${user.username}")
        } yield res
    }
