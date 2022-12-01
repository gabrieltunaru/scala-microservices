package com.cannondev.business.algebra

import cats.effect.Async
import cats.implicits.{catsSyntaxApplicativeError, toFlatMapOps, toFunctorOps}
import com.cannondev.business.domain.RequestEvent
import com.cannondev.business.errors.UserNotFound
import com.cannondev.business.storage.daos.event.{EventModel, EventRepository}
import com.cannondev.business.storage.daos.profile.ProfileRepository
import com.cannondev.business.util.AuthClient
import com.cannondev.business.util.OptionUtil.*
import org.slf4j.LoggerFactory

trait EventAlgebra[F[_]]:

  def insert(event: RequestEvent, token: String): F[String]
  def getAll(token: String): F[List[EventModel]]

object EventAlgebra:
  def apply[F[_]: Async](using
      profileRepository: ProfileRepository[F],
      eventRepository: EventRepository[F],
      authClient: AuthClient[F],
      profileAlgebra: ProfileAlgebra[F]
  ): EventAlgebra[F] = EventAlgebraImpl[F]
