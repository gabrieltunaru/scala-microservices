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

trait ProfileAlgebra[F[_]]:

  def insert(profile: RequestProfile, token: String): F[String]
  def find(token: String): F[Option[ProfileModel]]

object ProfileAlgebra:
  def apply[F[_]: Async](using
      profileRepository: ProfileRepository[F],
      authClient: AuthClient[F]
  ) = ProfileAlgebraImpl[F]
