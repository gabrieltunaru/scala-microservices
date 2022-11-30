package com.cannondev.business.storage.daos

import cats.effect.*
import cats.implicits.*
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

import java.time.LocalDateTime
import java.util.UUID

trait EventRepository[F[_]]:
  def insert(user: EventModel): F[Unit]
  def find(): F[List[EventModel]]



object EventRepository:

  private val insertOne: Command[EventModel] =
    sql"INSERT INTO public.event VALUES ($uuid, $varchar, $varchar, $varchar, $timestamp, $uuid);".command
      .gcontramap[EventModel]

  private val findAll: Query[Void, EventModel] =
    sql"SELECT * FROM public.event"
      .query(uuid ~ varchar ~ varchar ~ varchar ~ timestamp ~ uuid)
      .gmap[EventModel]

  def apply[F[_]: Async](using
      session: Resource[F, Session[F]],
      ev: MonadCancel[F, Throwable]
  ): EventRepository[F] = new EventRepository[F] {
    def insert(event: EventModel): F[Unit] =
      session.use { s =>
        s.prepare(insertOne).use(_.execute(event)).void
      }
    def find(): F[List[EventModel]] =
      session.use { s =>
        s.execute(findAll)
      }
  }
