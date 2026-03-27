package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateEventRequest(val title: String, val description: String)

@Serializable
data class UpdateEventRequest(val title: String, val description: String)