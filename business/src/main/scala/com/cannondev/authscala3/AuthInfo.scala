package com.cannondev.authscala3

import cats.Applicative
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.{Decoder, Encoder, Json}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.*

trait AuthInfo[F[_]]:
  def hello(n: AuthInfo.User): F[AuthInfo.User]

object AuthInfo:
  def apply[F[_]](using ev: AuthInfo[F]): AuthInfo[F] = ev

  /**
    * More generally you will want to decouple your edge representations from
    * your internal data structures, however this shows how you can
    * create encoders for your data.
    **/
  case class User(username: String, password: String)

  given Decoder[User] = Decoder.derived[User]
  given [F[_]: Concurrent]: EntityDecoder[F, User] = jsonOf


