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

trait UserAlgebra[F[_]]:

  def register(credentials: User): F[Token]
  def login(credentials: User): F[Token]

object UserAlgebra:
  def apply[F[_]](using
      session: Resource[F, Session[F]],
      cfg: AppConfig,
      F: Async[F]
  ): UserAlgebra[F] = UserAlgebraImpl()
