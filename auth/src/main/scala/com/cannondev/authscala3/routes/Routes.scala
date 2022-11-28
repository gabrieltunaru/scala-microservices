package com.cannondev.authscala3.routes

import cats.effect.Resource
import cats.effect.kernel.Concurrent
import com.cannondev.authscala3.AuthInfo.User
import com.cannondev.authscala3.algebra.UserAlgebra
import com.cannondev.authscala3.config.DbConfig.AppConfig
import com.cannondev.authscala3.errors.MissingHeader
import com.cannondev.authscala3.util.Jwt
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Authorization
import org.http4s.{Headers, HttpRoutes}
import skunk.Session
import tsec.passwordhashers.PasswordHasher
import tsec.passwordhashers.jca.BCrypt
import cats.implicits.*
import io.circe.syntax.*
import io.circe.generic.auto.*
import org.http4s.circe.jsonEncoder

class Routes[F[_]](using
    userAlgebra: UserAlgebra[F],
    session: Resource[F, Session[F]],
    hasher: PasswordHasher[F, BCrypt],
    F: Concurrent[F]
):

  val dsl = new Http4sDsl[F] {}
  import dsl.*

  private val errorHandler = ErrorHandler()

  private def getAuthToken(headers: Headers): F[String] =
    headers.get(Authorization.name) match {
      case Some(value) => F.pure(value.head.value)
      case None        => F.raiseError(MissingHeader)
    }

  def registerRoute(cfg: AppConfig): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "register" =>
        val route = for
          credentials <- req.as[User]
          token <- userAlgebra.register(credentials)
          res <- Ok(token.asJson)
        yield res
        route.recoverWith(errorHandler.handleErrors)
      case req @ POST -> Root / "login" =>
        val route = for
          credentials <- req.as[User]
          token <- userAlgebra.login(credentials)
          res <- Ok(token.asJson)
        yield res
        route.recoverWith(errorHandler.handleErrors)
      case req @ POST -> Root / "validate" =>
        val route = for
          token <- getAuthToken(req.headers)
          userId <- Jwt.decode(token, cfg.hasingPrivateKey)
          res <- Ok(userId)
        yield res
        route.recoverWith(errorHandler.handleErrors)
    }
