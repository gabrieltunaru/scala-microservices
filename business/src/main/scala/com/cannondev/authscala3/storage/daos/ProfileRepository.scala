package com.cannondev.authscala3.storage.daos

import cats.effect.*
import cats.implicits.*
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

import java.util.UUID

trait ProfileRepository[F[_]] {
  def insert(user: ProfileModel): F[Unit]
  def find(username: String): F[Option[ProfileModel]]
}

case class ProfileModel(uuid: UUID = UUID.randomUUID(), userId: UUID, name: String, password: String)

object ProfileRepository {

  private val insertOne: Command[ProfileModel] = {
    sql"INSERT INTO public.user VALUES ($uuid, $uuid, $varchar, $varchar);".command
      .gcontramap[ProfileModel]
  }

  private val findOne: Query[String, ProfileModel] = {
    sql"SELECT * FROM public.user WHERE user_id=$varchar"
      .query(uuid ~ uuid ~ varchar ~ varchar)
      .gmap[ProfileModel]
  }

  def apply[F[_]: Concurrent]()(implicit
      session: Resource[F, Session[F]],
      ev: MonadCancel[F, Throwable]
  ): ProfileRepository[F] = {
    new ProfileRepository[F] {
      def insert(profile: ProfileModel): F[Unit] =
        session.use { s =>
          s.prepare(insertOne).use(_.execute(profile)).void
        }
      def find(userId: String): F[Option[ProfileModel]] =
        session.use { s =>
          s.prepare(findOne).use(_.stream(userId, 32).compile.last)
        }
    }
  }
}
