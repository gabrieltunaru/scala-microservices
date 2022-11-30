package com.cannondev.business.routes

import cats.effect.Async
import org.http4s.Status
import cats.implicits.*
import cats.effect.implicits.*
import com.cannondev.business.errors.*
import com.cannondev.business.errors.ErrorCode.*
import org.http4s.Response
import cats.Applicative
import org.http4s.dsl.Http4sDsl
import io.circe.syntax.*
import io.circe.generic.auto.*
import org.http4s.circe.*

class ErrorHandler[F[_]: Applicative] {

  private val dsl = new Http4sDsl[F] {}
  import dsl._

  def handleErrors: PartialFunction[Throwable, F[Response[F]]] = {
    case br: ApiBadRequest => BadRequest.apply(br.extract().asJson)
    case au: ApiUnauthorized =>
      BadRequest.apply(
        au.extract().asJson.toString
      ) // TODO: make 401 (http4s requires to return www-authenticate header)
    case fb: ApiForbidden => Forbidden(fb.extract().asJson)
    case e =>
      e.printStackTrace()
      InternalServerError()
  }

}
