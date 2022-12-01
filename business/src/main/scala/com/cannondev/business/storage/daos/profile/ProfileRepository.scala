package com.cannondev.business.storage.daos.profile

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

  def apply[F[_]: Async](using
      session: Resource[F, Session[F]],
      ev: MonadCancel[F, Throwable]
  ): ProfileRepository[F] = ProfileRepositoryImpl[F]

class ProfileRepositoryImpl[F[_]: Async](using
    session: Resource[F, Session[F]]
) extends ProfileRepository[F]
    with ProfileSql:
  def insert(profile: ProfileModel): F[Unit] =
    session.use { s =>
      s.prepare(insertOne).use(_.execute(profile)).void
    }
  def find(userId: String): F[Option[ProfileModel]] =
    session.use { s =>
      s.prepare(findOne).use(_.stream(UUID.fromString(userId), 32).compile.last)
    }
