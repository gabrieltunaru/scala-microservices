package com.cannondev.business.storage
import cats.effect.*
import com.cannondev.business.config.DbConfig
import skunk.*
import skunk.implicits.*
import skunk.codec.all.*
import natchez.Trace.Implicits.noop
import DbConfig.DatabaseConfig

object DatabaseConnection {
  def getSession(dbConfig: DatabaseConfig): Resource[IO, Session[IO]] = {
    Session.single(
      host = dbConfig.uri,
      port = dbConfig.port,
      user = dbConfig.username,
      database = dbConfig.dbName,
      password = Some(dbConfig.password)
    )
  }

  def run(session: Resource[IO, Session[IO]]): IO[Unit] =
    session.use { s =>
      for {
        d <- s.unique(sql"select current_date".query(date))
        _ <- IO.println(s"The current date is $d.")
      } yield IO.unit
    }

}
