
package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class EventCreateRequest(
    val title: String,
    val description: String? = null
)

@Serializable
data class EventResponse(
    val id: Long,
    val title: String,
    val description: String? = null,
    val ownerId: Long
)

