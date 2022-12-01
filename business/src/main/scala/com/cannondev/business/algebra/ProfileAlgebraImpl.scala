package com.cannondev.business.algebra

import cats.effect.Async
import cats.implicits.{catsSyntaxApplicativeError, toFlatMapOps, toFunctorOps}
import com.cannondev.business.domain.RequestProfile
import com.cannondev.business.storage.daos.*
import com.cannondev.business.storage.*
import com.cannondev.business.storage.daos.profile.{ProfileModel, ProfileRepository}
import com.cannondev.business.util.AuthClient
import org.slf4j.LoggerFactory

import java.util.UUID

class ProfileAlgebraImpl[F[_]: Async](using profileRepository: ProfileRepository[F], authClient: AuthClient[F]) extends ProfileAlgebra[F]:

  private def logger = LoggerFactory.getLogger(this.getClass)

  def insert(profile: RequestProfile, token: String): F[String] = for
    userId <- authClient.getUserId(token)
    _ = logger.info(s"Got user id: $userId")
    profileModel = ProfileModel(userId = UUID.fromString(userId), name = profile.name, address = profile.address)
    _ <- profileRepository.insert(profileModel)
  yield userId

  def find(token: String): F[Option[ProfileModel]] =
    for
      userId <- authClient.getUserId(token)
      profile <- profileRepository.find(userId)
    yield profile
