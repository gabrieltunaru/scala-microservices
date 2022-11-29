package com.cannondev.business.storage.daos

import cats.effect.Concurrent
import com.cannondev.business.domain.RequestProfile
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

import java.time.LocalDateTime
import java.util.UUID

case class EventModel(
    uuid: UUID = UUID.randomUUID(),
    name: String,
    address: String,
    description: String,
    time: LocalDateTime,
    owner: UUID
)

object EventModel:
  given Encoder[EventModel] = deriveEncoder
  given [F[_]: Concurrent]: EntityEncoder[F, EventModel] = jsonEncoderOf[F, EventModel]
  given [F[_]: Concurrent]: EntityEncoder[F, List[EventModel]] = jsonEncoderOf[F, List[EventModel]]
