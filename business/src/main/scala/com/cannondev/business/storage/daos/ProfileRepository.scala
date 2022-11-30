package com.cannondev.business.storage.daos

import cats.effect.*
import cats.implicits.{catsSyntaxApplicativeError, toFlatMapOps, toFunctorOps}
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

import java.util.UUID

trait ProfileRepository[F[_]] {
  def insert(user: ProfileModel): F[Unit]
  def find(userId: String): F[Option[ProfileModel]]
}

object ProfileRepository:

  private val insertOne: Command[ProfileModel] =
    sql"INSERT INTO public.profile VALUES ($uuid, $uuid, $varchar, $varchar);".command
      .gcontramap[ProfileModel]

  private val findOne: Query[UUID, ProfileModel] =
    sql"SELECT * FROM public.profile WHERE user_id=$uuid"
      .query(uuid ~ uuid ~ varchar ~ varchar)
      .gmap[ProfileModel]
  def apply[F[_]: Async](using
      session: Resource[F, Session[F]],
      ev: MonadCancel[F, Throwable]
  ): ProfileRepository[F] = new ProfileRepository[F] {
    def insert(profile: ProfileModel): F[Unit] =
      session.use { s =>
        s.prepare(insertOne).use(_.execute(profile)).void
      }
    def find(userId: String): F[Option[ProfileModel]] =
      session.use { s =>
        s.prepare(findOne).use(_.stream(UUID.fromString(userId), 32).compile.last)
      }
  }
