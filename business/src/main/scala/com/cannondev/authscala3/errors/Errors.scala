package com.cannondev.authscala3.errors

sealed trait Errors extends Exception

case class DatabaseNotFound(identifier: String) extends Errors
case object MissingHeader extends Errors
case class TokenInvalid(message: String) extends Errors
