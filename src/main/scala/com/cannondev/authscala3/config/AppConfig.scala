package com.cannondev.authscala3.config

import ciris.ConfigValue
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import ciris._
import ciris.refined._
import scala.concurrent.duration._

case class DabConfig(
    uri: String,
    username: String,
    password: String,
    dbName: String,
    port: Int = 5432
)
case class AppConfig(
    dbConfig: DabConfig
)
