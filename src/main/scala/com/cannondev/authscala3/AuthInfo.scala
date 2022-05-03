package com.cannondev.authscala3

import cats.Applicative
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.{Decoder, Encoder, Json}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.*

trait AuthInfo[F[_]]:
  def hello(n: AuthInfo.Info): F[AuthInfo.Info]

object AuthInfo:
  def apply[F[_]](using ev: AuthInfo[F]): AuthInfo[F] = ev

  /**
    * More generally you will want to decouple your edge representations from
    * your internal data structures, however this shows how you can
    * create encoders for your data.
    **/
  case class Info(username: String, password: String)

  given Decoder[Info] = Decoder.derived[Info]
  given [F[_]: Concurrent]: EntityDecoder[F, Info] = jsonOf


