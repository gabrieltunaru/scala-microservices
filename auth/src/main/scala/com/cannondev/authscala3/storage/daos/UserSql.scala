package com.cannondev.authscala3.storage.daos

import skunk.{Command, Query}
import skunk.codec.all.{uuid, varchar}
import skunk.implicits.sql

trait UserSql:

  protected val insertOne: Command[UserModel] =
    sql"INSERT INTO public.user VALUES ($uuid, $varchar, $varchar);".command
      .gcontramap[UserModel]

  protected val findOne: Query[String, UserModel] =
    sql"SELECT * FROM public.user WHERE username=$varchar"
      .query(uuid ~ varchar ~ varchar)
      .gmap[UserModel]
