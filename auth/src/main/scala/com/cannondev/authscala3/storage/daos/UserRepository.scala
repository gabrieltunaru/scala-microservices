package com.cannondev.authscala3.storage.daos

import cats.effect.*
import cats.implicits.*
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*
import com.cannondev.authscala3.AuthInfo.User

import java.util.UUID

trait UserRepository[F[_]]:
  def insert(user: UserModel): F[Unit]
  def find(username: String): F[Option[UserModel]]

object UserRepository:

  def apply[F[_]: Concurrent](using session: Resource[F, Session[F]]): UserRepository[F] =
    UserRepositoryImpl[F]

class UserRepositoryImpl[F[_]: Concurrent](using session: Resource[F, Session[F]])
    extends UserRepository[F]
    with UserSql:
  def insert(user: UserModel): F[Unit] =
    session.use { s =>
      s.prepare(insertOne).use(_.execute(user)).void
    }

  def find(username: String): F[Option[UserModel]] =
    session.use { s =>
      s.prepare(findOne).use(_.stream(username, 32).compile.last)
    }
