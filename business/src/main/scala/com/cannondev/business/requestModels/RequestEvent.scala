package com.cannondev.business.requestModels

import java.time.LocalDateTime
import java.util.UUID

case class RequestEvent(
    name: String,
    address: String,
    description: String,
    time: LocalDateTime
)
