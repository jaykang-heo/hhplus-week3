package com.example.hhplusweek3.domain.command

data class MarkOutboxEventProcessedCommand(
    val eventId: String,
)
