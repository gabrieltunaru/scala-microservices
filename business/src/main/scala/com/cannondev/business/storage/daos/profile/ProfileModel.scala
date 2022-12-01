package com.cannondev.business.storage.daos.profile

import java.util.UUID

case class ProfileModel(
    uuid: UUID = UUID.randomUUID(),
    userId: UUID,
    name: String,
    address: String
)
