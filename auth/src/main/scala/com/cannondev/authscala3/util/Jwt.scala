package com.cannondev.authscala3.util

import cats.effect.kernel.Concurrent
import com.cannondev.authscala3.errors.{InvalidToken}
import com.cannondev.authscala3.util.Jwt.logger
import org.slf4j.LoggerFactory

import java.time.Instant
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}

import scala.util.{Failure, Success}

object Jwt {

  private val logger = LoggerFactory.getLogger(this.getClass)

  case class Token(token: String)

  def encode(secret: String): String = {
    val claim = JwtClaim(
      expiration = Some(Instant.now.plusSeconds(157784760).getEpochSecond),
      issuedAt = Some(Instant.now.getEpochSecond)
    )
    val algo = JwtAlgorithm.HS256
    JwtCirce.encode(claim, secret, algo)
  }

  def decode[F[_]](token: String, key: String)(implicit F: Concurrent[F]): F[String] = {
    val tokenWithoutBearer = token.slice(7, token.length)
    logger.info(s"Decoding token $tokenWithoutBearer")
    JwtCirce.decode(tokenWithoutBearer, key, List(JwtAlgorithm.HS256)) match {
      case Failure(exception) =>
        val errorMessage = s"Invalid JWT token: $exception"
        logger.warn(errorMessage)
        F.raiseError(InvalidToken)
      case Success(value) => F.pure(value.content)
    }
  }
}
