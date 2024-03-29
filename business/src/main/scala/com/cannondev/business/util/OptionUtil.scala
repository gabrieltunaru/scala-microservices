package com.cannondev.business.util

import cats.effect.kernel.Concurrent
import cats.{Monad, MonadError}
import com.cannondev.business.errors.*

object OptionUtil {
  extension[F[_], T](option: Option[T]) {
    def orRaise(me: ApiError)(implicit F: Concurrent[F]): F[T] = {
      option match
        case Some(value) => F.pure(value)
        case None => F.raiseError(me)
    }
  }
}
