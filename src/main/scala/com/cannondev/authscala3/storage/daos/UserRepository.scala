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
}

case class UserModel(uuid: UUID = UUID.randomUUID(), username: String, password: String)

object UserRepository {

  private val insertOne: Command[UserModel] = {
    sql"INSERT INTO public.user VALUES ($uuid, $varchar, $varchar);".command
      .gcontramap[UserModel]
  }

  def apply[F[_]]()(implicit session: Resource[F, Session[F]], ev: MonadCancel[F, Throwable]): UserRepository[F] = {
    new UserRepository[F] {
      def insert(user: UserModel): F[Unit] =
        session.use { s =>
          s.prepare(insertOne).use(_.execute(user)).void
        }
    }
  }
}
