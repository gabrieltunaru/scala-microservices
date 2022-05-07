package com.cannondev.authscala3

import cats.Monad
import cats.effect.{IO, Resource, Sync}
import cats.effect.kernel.Concurrent
import cats.implicits.*
import com.cannondev.authscala3.storage.daos.{UserModel, UserRepository}
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import skunk.Session
import tsec.passwordhashers.PasswordHasher
import tsec.passwordhashers.jca.BCrypt

object Authscala3Routes {

  def registerRoute[F[_]: Concurrent](implicit
      hasher: PasswordHasher[F, BCrypt],
      session: Resource[F, Session[F]]
  ): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    implicit val decoder: EntityDecoder[F, AuthInfo.User] = jsonOf[F, AuthInfo.User]
    HttpRoutes.of[F] { case req @ POST -> Root / "register" =>
      for {
        user <- req.as[AuthInfo.User]
        pwHash <- BCrypt.hashpw[F](user.password)
        userWithHashedPassword = UserModel(username = user.username, password = pwHash)
        _ <- UserRepository().insert(userWithHashedPassword)
        res <- Ok(s"Registered user ${user.username}")
      } yield res
    }
  }
}
