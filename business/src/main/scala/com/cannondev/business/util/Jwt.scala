package com.cannondev.business.util

import cats.effect.kernel.Concurrent
import cats.{Monad, MonadError, MonadThrow}
import org.slf4j.LoggerFactory

import java.time.Instant
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}

import scala.util.{Failure, Success}

object Jwt:

  def encode(secret: String): String = {
    val claim = JwtClaim(
      expiration = Some(Instant.now.plusSeconds(157784760).getEpochSecond),
      issuedAt = Some(Instant.now.getEpochSecond)
    )
    val algo = JwtAlgorithm.RS256
    JwtCirce.encode(claim, secret, algo)
  }
