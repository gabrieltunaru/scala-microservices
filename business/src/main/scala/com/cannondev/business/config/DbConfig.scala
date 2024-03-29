package com.cannondev.business.config

import cats.implicits.{catsSyntaxApplicativeError, toFlatMapOps, toFunctorOps}
import cats.syntax.parallel.*
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
      authServerUrl: String
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
      env("AUTH_SERVER_URL").as[String].default("http://localhost:8080")
    ).parMapN(AppConfig.apply)

