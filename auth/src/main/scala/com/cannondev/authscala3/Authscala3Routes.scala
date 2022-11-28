package com.cannondev.authscala3

import cats.Monad
import cats.data.{EitherT, OptionT}
import cats.effect.{IO, Resource, Sync}
import cats.effect.kernel.Concurrent
import cats.implicits.*
import org.http4s.Status.{BadRequest, Forbidden, NotFound, Ok, Unauthorized}
import org.http4s.{EntityDecoder, Headers, HttpRoutes, Response}
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import skunk.Session
import tsec.common.{VerificationFailed, Verified}
import tsec.passwordhashers.{PasswordHash, PasswordHasher}
import tsec.passwordhashers.jca.BCrypt
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.circe.jsonEncoder
import org.http4s.headers.Authorization

import com.cannondev.authscala3.AuthInfo.User
import com.cannondev.authscala3.config.DbConfig.AppConfig
import com.cannondev.authscala3.errors.*
import com.cannondev.authscala3.storage.daos.{UserModel, UserRepository}
import com.cannondev.authscala3.util.OptionUtil._
import com.cannondev.authscala3.algebra.UserAlgebra
import com.cannondev.authscala3.routes.ErrorHandler
import util.Jwt
import util.Jwt.Token

import cats.implicits.*
import cats.effect.implicits.*

object Authscala3Routes:

  private def getAuthToken[F[_]](headers: Headers)(implicit F: Concurrent[F]): F[String] =
    headers.get(Authorization.name) match {
      case Some(value) => F.pure(value.head.value)
      case None        => F.raiseError(MissingHeader)
    }

  def registerRoute[F[_]: Concurrent](cfg: AppConfig)(implicit
      userAlgebra: UserAlgebra[F],
      session: Resource[F, Session[F]],
      hasher: PasswordHasher[F, BCrypt]
  ): HttpRoutes[F] =
    val dsl = new Http4sDsl[F] {}
    import dsl._

    val errorHandler = ErrorHandler()

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
