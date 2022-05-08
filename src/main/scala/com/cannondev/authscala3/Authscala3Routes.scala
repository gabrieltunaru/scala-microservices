package com.cannondev.authscala3

import cats.Monad
import cats.data.{EitherT, OptionT}
import cats.effect.{IO, Resource, Sync}
import cats.effect.kernel.Concurrent
import cats.implicits.*
import com.cannondev.authscala3.AuthInfo.User
import com.cannondev.authscala3.errors.{UserNotFound, WrongPassword}
import com.cannondev.authscala3.storage.daos.{UserModel, UserRepository}
import org.http4s.Status.{BadRequest, NotFound, Ok}
import org.http4s.{EntityDecoder, HttpRoutes, Response}
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import skunk.Session
import tsec.common.{VerificationFailed, Verified}
import tsec.passwordhashers.{PasswordHash, PasswordHasher}
import tsec.passwordhashers.jca.BCrypt

object Authscala3Routes {

  private def checkPassword[F[_]](user: User, dbUserO: Option[UserModel])(implicit
      hasher: PasswordHasher[F, BCrypt],
      F: Concurrent[F]
  ): F[Boolean] = {
    dbUserO match {
      case Some(dbUser) =>
        for {
          isPasswordRight <- BCrypt.checkpwBool[F](user.password, PasswordHash(dbUser.password))
          res <- if (isPasswordRight) F.pure(true) else F.raiseError(WrongPassword(user.username))
        } yield res
      case None => F.raiseError(UserNotFound(user.username))
    }
  }

  def registerRoute[F[_]: Concurrent](implicit
      session: Resource[F, Session[F]],
      hasher: PasswordHasher[F, BCrypt]
  ): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    implicit val decoder: EntityDecoder[F, AuthInfo.User] = jsonOf[F, AuthInfo.User]
    import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

    HttpRoutes.of[F] {
      case req @ POST -> Root / "register" =>
        for {
          user <- req.as[AuthInfo.User]
          pwHash <- BCrypt.hashpw[F](user.password)
          userWithHashedPassword = UserModel(username = user.username, password = pwHash)
          _ <- UserRepository().insert(userWithHashedPassword)
          res <- Ok(s"Registered user ${user.username}")
        } yield res
      case req @ POST -> Root / "login" =>
        val result = for {
          user <- req.as[AuthInfo.User]
          dbUserO <- UserRepository().find(user.username)
          passwordResult <- checkPassword(user, dbUserO)
          res <- Ok(passwordResult)
        } yield res
        result.recoverWith {
          case UserNotFound(username)  => BadRequest(s"User $username not found")
          case WrongPassword(username) => BadRequest(s"Wrong password for user $username")
        }
    }
  }
}
