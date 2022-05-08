package com.cannondev.authscala3.storage.daos

import cats.effect.*
import cats.implicits.*
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*
import com.cannondev.authscala3.AuthInfo.User

import java.util.UUID

trait UserRepository[F[_]] {
  def insert(user: UserModel): F[Unit]
  def find(username: String): F[Option[UserModel]]
}

case class UserModel(uuid: UUID = UUID.randomUUID(), username: String, password: String)

object UserRepository {

  private val insertOne: Command[UserModel] = {
    sql"INSERT INTO public.user VALUES ($uuid, $varchar, $varchar);".command
      .gcontramap[UserModel]
  }

  private val findOne: Query[String, UserModel] = {
    sql"SELECT * FROM public.user WHERE username=$varchar"
      .query(uuid ~ varchar ~ varchar)
      .gmap[UserModel]
  }

  def apply[F[_]: Concurrent]()(implicit
      session: Resource[F, Session[F]],
      ev: MonadCancel[F, Throwable]
  ): UserRepository[F] = {
    new UserRepository[F] {
      def insert(user: UserModel): F[Unit] =
        session.use { s =>
          s.prepare(insertOne).use(_.execute(user)).void
        }
      def find(username: String): F[Option[UserModel]] =
        session.use { s =>
          s.prepare(findOne).use(_.stream(username, 32).compile.last)
        }
    }
  }
}
