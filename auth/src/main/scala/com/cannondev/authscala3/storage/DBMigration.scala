package com.cannondev.authscala3.storage

import cats.effect.Sync
import cats.implicits.*
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.flywaydb.core.api.Location
import org.flywaydb.core.Flyway

import scala.jdk.CollectionConverters.*
import com.cannondev.authscala3.config.DbConfig.DatabaseConfig
import org.slf4j.LoggerFactory

object DBMigration {

  private def logger = LoggerFactory.getLogger(this.getClass)
  def migrate[F[_]: Sync](config: DatabaseConfig): F[Int] =
    Sync[F].delay {
      logger.info(
        "Running migrations from locations: "
      )
      val count = unsafeMigrate(config)
      logger.info(s"Executed $count migrations")
      count
    }

  private def unsafeMigrate(config: DatabaseConfig): Int = {
    val m: FluentConfiguration = Flyway.configure
      .dataSource(
        s"jdbc:postgresql://${config.uri}:${config.port}/${config.dbName}",
        config.username,
        config.password
      )
      .group(true)
      .outOfOrder(false)
      .table("databasechangelog")
      .locations(
        List("classpath:db/migration")
          .map(new Location(_))
          .toList: _*
      )
      .baselineOnMigrate(true)

    logValidationErrorsIfAny(m)
    m.load().migrate().migrationsExecuted
  }

  private def logValidationErrorsIfAny(m: FluentConfiguration): Unit = {
    val validated = m
      .ignorePendingMigrations(true)
      .load()
      .validateWithResult()

    if !validated.validationSuccessful then
      for error <- validated.invalidMigrations.asScala do
        logger.warn(s"""
                       |Failed validation:
                       |  - version: ${error.version}
                       |  - path: ${error.filepath}
                       |  - description: ${error.description}
                       |  - errorCode: ${error.errorDetails.errorCode}
                       |  - errorMessage: ${error.errorDetails.errorMessage}
        """.stripMargin.strip)
  }
}
