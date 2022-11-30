package com.cannondev.business.config

import cats.implicits._
import cats.effect.IO
import ciris._

object DbConfig:
  final case class DatabaseConfig(
      uri: String,
      username: String,
      password: String,
      dbName: String,
      port: Int = 5432
  )

  final case class AppConfig(
      dbConfig: DatabaseConfig,
      publicKey: String
  )

  def dbConfig: ConfigValue[IO, DatabaseConfig] =
    (
      env("DB_URI").as[String].default("0.0.0.0"),
      env("DB_USERNAME").as[String].default("scalamicro"),
      env("DB_PASSWORD").as[String].default("scalamicro"),
      env("DB_NAME").as[String].default("scalamicro_business"),
      env("PORT").as[Int].default(5432)
    ).parMapN(DatabaseConfig.apply)

  def appConfig: ConfigValue[IO, AppConfig] =
    (
      dbConfig.as[DatabaseConfig],
      env("PUBLIC_KEY").as[String].default("thisisapublickey")
    ).parMapN(AppConfig.apply)

