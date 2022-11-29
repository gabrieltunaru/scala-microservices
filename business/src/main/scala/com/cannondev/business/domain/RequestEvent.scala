package com.cannondev.business.domain

import cats.effect.Concurrent
import io.circe.Decoder
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

import java.time.LocalDateTime
import java.util.UUID

case class RequestEvent(
    name: String,
    address: String,
    description: String,
    time: LocalDateTime
)

object RequestEvent:
  given Decoder[RequestEvent] = Decoder.derived[RequestEvent]
  given [F[_]: Concurrent]: EntityDecoder[F, RequestEvent] = jsonOf
