package com.cannondev.authscala3.util

import java.time.Instant
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}

object Jwt {

  case class Token(token: String)

  def encode(secret: String): String = {
    val claim = JwtClaim(
      expiration = Some(Instant.now.plusSeconds(157784760).getEpochSecond),
      issuedAt = Some(Instant.now.getEpochSecond)
    )
    val algo = JwtAlgorithm.HS256
    JwtCirce.encode(claim, secret, algo)
  }
}
