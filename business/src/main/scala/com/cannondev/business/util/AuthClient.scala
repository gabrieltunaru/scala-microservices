package com.cannondev.business.util

import cats.Monad
import cats.effect.{Async, IO}
import cats.effect.kernel.Concurrent
import com.cannondev.business.config.DbConfig.AppConfig
import org.http4s.*
import org.http4s.headers.{Accept, Authorization}
import org.http4s.client.dsl.io.*
import org.http4s.headers.*
import org.http4s.Method.*
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.circe.jsonOf
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.blaze.client.BlazeClientBuilder

import scala.concurrent.ExecutionContext.Implicits.global

class AuthClient[F[_]: Async](appConfig: AppConfig):

  private val httpClient = BlazeClientBuilder[F]
  def getUserId(token: String): F[String] =
    implicit val decoder: EntityDecoder[F, String] = jsonOf[F, String]

    val request: Request[F] =
      Request[F](
        method = POST,
        uri = Uri.unsafeFromString(s"${appConfig.authServerUrl}/api/validate"),
        headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token.slice(7, token.length))))
      ).withEntity(token)

    httpClient.resource.use(client => {
      client.expect[String](request)
    })
