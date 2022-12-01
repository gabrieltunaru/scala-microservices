package com.cannondev.authscala3.storage.daos

import java.util.UUID

case class UserModel(
    uuid: UUID = UUID.randomUUID(),
    username: String,
    password: String
)
