package com.cannondev.authscala3.errors

sealed trait Errors extends Exception

case class WrongPassword(username: String) extends Errors
case class UserNotFound(username: String) extends Errors
