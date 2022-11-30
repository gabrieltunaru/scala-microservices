package com.cannondev.business.routes

import cats.Monad
import cats.data.{EitherT, OptionT}
import cats.effect.{Async, IO, Resource, Sync}
import cats.effect.kernel.Concurrent
import cats.implicits.*
import com.cannondev.business.algebra.{EventAlgebra, ProfileAlgebra}
import com.cannondev.business.config.DbConfig.AppConfig
import com.cannondev.business.errors.*
import com.cannondev.business.domain.*
import com.cannondev.business.storage.daos.*
import com.cannondev.business.util.OptionUtil.*
import com.cannondev.business.domain.RequestEvent.*
import skunk.Session
import tsec.common.{VerificationFailed, Verified}
import tsec.passwordhashers.{PasswordHash, PasswordHasher}
import tsec.passwordhashers.jca.BCrypt
import com.cannondev.business.util.*
import org.http4s.Status.{BadRequest, NotFound, Ok}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.*
import org.http4s.{Headers, HttpRoutes}
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.slf4j.LoggerFactory

import java.util.UUID
import io.circe.syntax.*
import io.circe.generic.auto.*

object Routes:

  private val logger = LoggerFactory.getLogger(this.getClass)

  def routes[F[_]: Async](cfg: AppConfig)(using
      session: Resource[F, Session[F]],
      hasher: PasswordHasher[F, BCrypt],
      profileAlgebra: ProfileAlgebra[F],
      eventAlgebra: EventAlgebra[F]
  ): HttpRoutes[F] =
    val dsl = new Http4sDsl[F] {}
    import dsl.*

    val errorHandler = ErrorHandler[F]

    HttpRoutes.of[F] {
      case req @ POST -> Root / "profile" =>
        val result = for
          profile <- req.as[RequestProfile]
          token <- getAuthToken(req.headers)
          userId <- profileAlgebra.insert(profile, token)
          res <- Ok(userId)
        yield res
        result.recoverWith(errorHandler.handleErrors)

      case req @ GET -> Root / "profile" =>
        val result = for
          token <- getAuthToken(req.headers)
          profile <- profileAlgebra.find(token)
          res <- Ok(profile)
        yield res
        result.recoverWith(errorHandler.handleErrors)

      case req @ POST -> Root / "event" =>
        val result = for
          event <- req.as[RequestEvent]
          token <- getAuthToken(req.headers)
          userId <- eventAlgebra.insert(event, token)
          res <- Ok(userId)
        yield res
        result.recoverWith(errorHandler.handleErrors)

      case req @ GET -> Root / "events" =>
        val result = for
          token <- getAuthToken(req.headers)
          events <- eventAlgebra.getAll(token)
          res <- Ok(events)
        yield res
        result.recoverWith(errorHandler.handleErrors)
    }
