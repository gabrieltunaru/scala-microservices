package com.cannondev.authscala3.algebra

import com.cannondev.authscala3.AuthInfo
import cats.effect.IO
import tsec.passwordhashers.jca.BCrypt
import cats.effect.kernel.Resource
import tsec.passwordhashers.PasswordHasher
import skunk.Session
import cats.implicits.*
import cats.effect.kernel.Async
import com.cannondev.authscala3.storage.daos.UserModel
import com.cannondev.authscala3.storage.daos.UserRepository
import com.cannondev.authscala3.errors.*
import com.cannondev.authscala3.util.Jwt.Token
import com.cannondev.authscala3.util.Jwt
import com.cannondev.authscala3.config.DbConfig.AppConfig
import io.circe.syntax.*
import io.circe.generic.auto.*

import com.cannondev.authscala3.AuthInfo.User
import tsec.passwordhashers.PasswordHash
import com.cannondev.authscala3.util.OptionUtil._

class UserAlgebra[F[_]](using
    session: Resource[F, Session[F]],
    cfg: AppConfig,
    F: Async[F]
):

  private def checkPassword(
      user: User,
      dbUserO: Option[UserModel]
  ): F[Boolean] =
    dbUserO match {
      case Some(dbUser) =>
        for
          isPasswordRight <- BCrypt
            .checkpwBool[F](user.password, PasswordHash(dbUser.password))
          res <-
            if isPasswordRight then F.pure(true)
            else F.raiseError(WrongPassword(user.username))
        yield res
      case None => F.raiseError(UserNotFound(user.username))
    }

  def register(credentials: User): F[Token] = for
    pwHash <- BCrypt.hashpw[F](credentials.password)
    userWithHashedPassword = UserModel(
      username = credentials.username,
      password = pwHash
    )
    _ <- UserRepository().insert(userWithHashedPassword)
    insertedUserO <- UserRepository().find(credentials.username)
    insertedUser: UserModel <- insertedUserO match {
      case Some(value) => F.pure(value)
      case None        => F.raiseError(UserNotFound(credentials.username))
    }
  yield Token(
    Jwt.encode(cfg.hasingPrivateKey, insertedUser.uuid.toString())
  )

  def login(credentials: User): F[Token] = for
    dbUserO <- UserRepository().find(credentials.username)
    passwordResult <- checkPassword(credentials, dbUserO)
    dbUser <- dbUserO.orElse(UserNotFound(credentials.username))
  yield Token(
    Jwt.encode(cfg.hasingPrivateKey, dbUser.uuid.toString)
  )
