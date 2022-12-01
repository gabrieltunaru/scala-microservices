package com.cannondev.business.storage.daos.event

import cats.effect.*
import cats.implicits.{catsSyntaxApplicativeError, toFlatMapOps, toFunctorOps}
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

import java.time.LocalDateTime
import java.util.UUID

trait EventRepository[F[_]]:
  def insert(user: EventModel): F[Unit]
  def find(): F[List[EventModel]]

object EventRepository:
  def apply[F[_]: Async](using
      session: Resource[F, Session[F]]): EventRepository[F] =
    EventRepositoryImpl[F]

class EventRepositoryImpl[F[_]: Async](using
    session: Resource[F, Session[F]]) extends EventRepository[F]
    with EventSql:

  def insert(event: EventModel): F[Unit] =
    session.use { s =>
      s.prepare(insertOne).use(_.execute(event)).void
    }
  def find(): F[List[EventModel]] =
    session.use { s =>
      s.execute(findAll)
    }
