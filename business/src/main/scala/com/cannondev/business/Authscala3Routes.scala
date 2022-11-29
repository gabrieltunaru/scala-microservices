package com.cannondev.business

import cats.Monad
import cats.data.{EitherT, OptionT}
import cats.effect.{Async, IO, Resource, Sync}
import cats.effect.kernel.Concurrent
import cats.implicits.*
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
import util.{AuthClient, Jwt}
import util.Jwt.Token

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


object Authscala3Routes:

  private val logger = LoggerFactory.getLogger(this.getClass)

  private def getAuthToken[F[_]](headers: Headers)(using F: Concurrent[F]): F[String] = {
    headers.get(Authorization.name) match {
      case Some(value) => F.pure(value.head.value)
      case None        => F.raiseError(MissingHeader)
    }
  }

  def registerRoute[F[_]: Async](cfg: AppConfig)(using
      session: Resource[F, Session[F]],
      hasher: PasswordHasher[F, BCrypt]
  ): HttpRoutes[F] =
    val dsl = new Http4sDsl[F] {}
    import dsl.*

    HttpRoutes.of[F] {
      case req @ POST -> Root / "profile" =>
        val result = for
          profile <- req.as[RequestProfile]
          token <- getAuthToken(req.headers)
          userId <- AuthClient().getUserId(token)
          _ = logger.info(s"Got user id: $userId")
          profileModel = ProfileModel(userId = UUID.fromString(userId), name = profile.name, address = profile.address)
          _ <- ProfileRepository().insert(profileModel)
          res <- Ok(userId)
        yield res
        result.recoverWith { case DatabaseNotFound(username) =>
          BadRequest(s"User $username not found")
        }

      case req @ GET -> Root / "profile" =>
        val result = for
          token <- getAuthToken(req.headers)
          userId <- AuthClient().getUserId(token)
          profile <- ProfileRepository().find(userId)
          res <- Ok(profile)
        yield res

        result.recoverWith { case DatabaseNotFound(username) =>
          BadRequest(s"User $username not found")
        }

      case req @ POST -> Root / "event" =>
        val result = for
          event <- req.as[RequestEvent]
          token <- getAuthToken(req.headers)
          userId <- AuthClient().getUserId(token)
          profileO <- ProfileRepository().find(userId)
          profile <- profileO.orRaise(UserNotFound(userId))
          eventModel = EventModel(
            name = event.name,
            description = event.description,
            time = event.time,
            owner = profile.uuid,
            address = event.address
          )
          _ <- EventRepository().insert(eventModel)
          res <- Ok(userId)
        yield res

        result.recoverWith {
          case DatabaseNotFound(username) =>
            BadRequest(s"User $username not found")
          case InvalidToken => BadRequest()
        }

      case req @ GET -> Root / "events" =>
        val result = for
          token <- getAuthToken(req.headers)
          _ <- AuthClient().getUserId(token)
          events <- EventRepository().find()
          res <- Ok(events)
        yield res

        result.recoverWith {
          case DatabaseNotFound(username) =>
            BadRequest(s"User $username not found")
          case InvalidToken => BadRequest()
        }
    }
