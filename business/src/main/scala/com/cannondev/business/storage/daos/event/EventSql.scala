package com.cannondev.business.storage.daos.event

import skunk.implicits.sql
import skunk.codec.all.{timestamp, uuid, varchar}
import skunk.{Command, Query, Void}

trait EventSql:

  protected val insertOne: Command[EventModel] =
    sql"INSERT INTO public.event VALUES ($uuid, $varchar, $varchar, $varchar, $timestamp, $uuid);".command
      .gcontramap[EventModel]

  protected val findAll: Query[Void, EventModel] =
    sql"SELECT * FROM public.event"
      .query(uuid ~ varchar ~ varchar ~ varchar ~ timestamp ~ uuid)
      .gmap[EventModel]

