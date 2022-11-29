package com.cannondev.business.domain

import cats.Applicative
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.*
import org.http4s.circe.*
import org.http4s.{EntityDecoder, EntityEncoder}

case class RequestProfile(name: String, address: String)

object RequestProfile:
  given Decoder[RequestProfile] = Decoder.derived[RequestProfile]
  given [F[_]: Concurrent]: EntityDecoder[F, RequestProfile] = jsonOf
