package com.cannondev.business.domain

import java.time.LocalDateTime

case class RequestEvent(
    name: String,
    address: String,
    description: String,
    time: LocalDateTime
)
