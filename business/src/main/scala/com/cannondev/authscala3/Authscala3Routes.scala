package com.cannondev.authscala3

import cats.Monad
import cats.data.{EitherT, OptionT}
import cats.effect.{Async, IO, Resource, Sync}
import cats.effect.kernel.Concurrent
import cats.implicits.*
import com.cannondev.authscala3.config.DbConfig.AppConfig
import com.cannondev.authscala3.errors.{DatabaseNotFound, MissingHeader, TokenInvalid}
import com.cannondev.authscala3.requestModels.RequestProfile
import com.cannondev.authscala3.storage.daos.{ProfileModel, ProfileRepository}
import org.http4s.Status.{BadRequest, NotFound, Ok}
import org.http4s.{Credentials, EntityDecoder, Headers, HttpRoutes, MediaType, Response}
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import skunk.Session
import tsec.common.{VerificationFailed, Verified}
import tsec.passwordhashers.{PasswordHash, PasswordHasher}
import tsec.passwordhashers.jca.BCrypt
import util.{AuthClient, Jwt}
import util.Jwt.Token
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.headers.{Accept, Authorization}
import org.slf4j.LoggerFactory

object Authscala3Routes {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private def getAuthToken[F[_]](headers: Headers)(implicit F: Concurrent[F]): F[String] = {
    headers.get(Authorization.name) match {
      case Some(value) => F.pure(value.head.value)
      case None        => F.raiseError(MissingHeader)
    }
  }

  def registerRoute[F[_]: Concurrent: Async](cfg: AppConfig)(implicit
      session: Resource[F, Session[F]],
      hasher: PasswordHasher[F, BCrypt]
  ): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    implicit val decoder: EntityDecoder[F, RequestProfile] = jsonOf[F, RequestProfile]
    import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
    HttpRoutes.of[F] { case req @ POST -> Root / "profile" =>
      val result = for {
        profile <- req.as[RequestProfile]
        token <- getAuthToken(req.headers)
        userId <- AuthClient().getUserId(token)
        _ = logger.info(s"Got user id: $userId")
        res <- Ok(token.toString)
      } yield res
      result.recoverWith {
        case DatabaseNotFound(username) =>
          BadRequest(s"User $username not found")
        case TokenInvalid(message) => BadRequest(message)
      }
    }
  }
}
