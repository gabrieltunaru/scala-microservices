package com.cannondev.business.algebra

import cats.effect.Async
import com.cannondev.business.domain.RequestEvent
import com.cannondev.business.storage.daos.{EventModel, EventRepository, ProfileRepository}
import com.cannondev.business.util.AuthClient
import org.slf4j.LoggerFactory
import cats.implicits.*
import com.cannondev.business.errors.UserNotFound
import com.cannondev.business.util.OptionUtil.*

class EventAlgebraImpl[F[_]: Async](using
    profileRepository: ProfileRepository[F],
    eventRepository: EventRepository[F],
    authClient: AuthClient[F],
    profileAlgebra: ProfileAlgebra[F]
) extends EventAlgebra[F]:

  def insert(event: RequestEvent, token: String): F[String] = for
    userId <- authClient.getUserId(token)
    profileO <- profileAlgebra.find(userId)
    profile <- profileO.orRaise(UserNotFound(userId))
    eventModel = EventModel(
      name = event.name,
      description = event.description,
      time = event.time,
      owner = profile.uuid,
      address = event.address
    )
    _ <- eventRepository.insert(eventModel)
  yield userId

  def getAll(token: String): F[List[EventModel]] = for
    _ <- authClient.getUserId(token)
    events <- eventRepository.find()
  yield events

  private def logger = LoggerFactory.getLogger(this.getClass)
