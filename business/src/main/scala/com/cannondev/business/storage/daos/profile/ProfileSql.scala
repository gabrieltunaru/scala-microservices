package com.cannondev.business.storage.daos.profile

import skunk.codec.all.{uuid, varchar}
import skunk.implicits.sql
import skunk.{Command, Query}

import java.util.UUID

trait ProfileSql:

  protected val insertOne: Command[ProfileModel] =
    sql"INSERT INTO public.profile VALUES ($uuid, $uuid, $varchar, $varchar);".command
      .gcontramap[ProfileModel]

  protected val findOne: Query[UUID, ProfileModel] =
    sql"SELECT * FROM public.profile WHERE user_id=$uuid"
      .query(uuid ~ uuid ~ varchar ~ varchar)
      .gmap[ProfileModel]
