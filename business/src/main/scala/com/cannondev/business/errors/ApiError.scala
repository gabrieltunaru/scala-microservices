package com.cannondev.business.errors


import io.circe.generic.auto.*
import ErrorCode.*

case class ErrorInfo(errorCode: ErrorCode, info: Option[String] = None)

sealed trait ApiError(errorCode: ErrorCode, info: Option[String] = None) extends RuntimeException {
  def extract(): ErrorInfo = ErrorInfo(errorCode, info)
}

sealed trait ApiBadRequest extends ApiError
sealed trait ApiUnauthorized extends ApiError
sealed trait ApiForbidden extends ApiError

case class DatabaseNotFound(username: String)
    extends ApiBadRequest,
      ApiError(ErrorCode.WrongPassword, Some(s"username $username"))

case class UserNotFound(username: String)
    extends ApiBadRequest,
      ApiError(ErrorCode.UserNotFound, Some(s"username $username"))

case object MissingHeader extends ApiUnauthorized, ApiError(ErrorCode.MissingHeader)
case object InvalidToken extends ApiForbidden, ApiError(ErrorCode.InvalidToken)
