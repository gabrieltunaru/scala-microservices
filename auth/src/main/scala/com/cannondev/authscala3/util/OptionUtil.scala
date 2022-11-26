package com.cannondev.authscala3.util

import cats.effect.kernel.Concurrent
import cats.{Monad, MonadError}
import com.cannondev.authscala3.errors.Errors

object OptionUtil:
  extension[F[_], T](option: Option[T]) {
    def orElse(me: Errors)(implicit F: Concurrent[F]): F[T] = {
      option match
        case Some(value) => F.pure(value)
        case None => F.raiseError(me)
    }
  }
