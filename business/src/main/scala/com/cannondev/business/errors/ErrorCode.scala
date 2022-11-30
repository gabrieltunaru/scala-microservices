package com.cannondev.business.errors
import io.circe.Encoder
import io.circe.{Decoder, Encoder, HCursor, Json}

enum ErrorCode:
  case WrongPassword extends ErrorCode
  case UserNotFound extends ErrorCode
  case MissingHeader extends ErrorCode
  case InvalidToken extends ErrorCode

object ErrorCode {
  implicit val e: Encoder[ErrorCode] = (a: ErrorCode) => Json.fromString(a.toString)
}
