package com.cannondev.business.requestModels

import cats.Applicative
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.{Decoder, Encoder, Json}
import org.http4s.circe.*
import org.http4s.{EntityDecoder, EntityEncoder}


case class RequestProfile(name: String, address: String)
given Decoder[RequestProfile] = Decoder.derived[RequestProfile]
given [F[_]: Concurrent]: EntityDecoder[F, RequestProfile] = jsonOf


