package com.cannondev.business

import cats.effect.kernel.Concurrent
import com.cannondev.business.errors.MissingHeader
import org.http4s.Headers
import org.http4s.headers.Authorization

package object routes {

  private def getAuthToken[F[_]](headers: Headers)(using F: Concurrent[F]): F[String] = {
    headers.get(Authorization.name) match {
      case Some(value) => F.pure(value.head.value)
      case None        => F.raiseError(MissingHeader)
    }
  }
}
