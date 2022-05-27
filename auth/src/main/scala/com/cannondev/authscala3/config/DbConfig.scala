package com.cannondev.authscala3.config

import cats.implicits._
import cats.effect.IO
import ciris._

object DbConfig {
  final case class DatabaseConfig(
      uri: String,
      username: String,
      password: String,
      dbName: String,
      port: Int = 5432
  )

  final case class AppConfig(
      dbConfig: DatabaseConfig,
      hasingPrivateKey: String
  )

  def dbConfig: ConfigValue[IO, DatabaseConfig] =
    (
      env("DB_URI").as[String].default("0.0.0.0"),
      env("DB_USERNAME").as[String],
      env("DB_PASSWORD").as[String],
      env("DB_NAME").as[String],
      env("PORT").as[Int].default(5432)
    ).parMapN(DatabaseConfig.apply)

  def appConfig: ConfigValue[IO, AppConfig] =
    (
      dbConfig.as[DatabaseConfig],
      env("PRIVATE_KEY").as[String].default("thisisaprivatekey")
    ).parMapN(AppConfig.apply)

}
