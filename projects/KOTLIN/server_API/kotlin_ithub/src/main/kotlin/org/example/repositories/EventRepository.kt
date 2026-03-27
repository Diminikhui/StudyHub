package org.example.repositories

data class EventRecord(
    val id: Long,
    val title: String,
    val description: String?,
    val ownerId: Long
)

interface EventRepository {
    suspend fun getAll(): List<EventRecord>
    suspend fun create(ownerId: Long, title: String, description: String?): EventRecord
    suspend fun deleteIfOwner(eventId: Long, ownerId: Long): Boolean
    suspend fun findById(id: Long): EventRecord?
    suspend fun update(id: Long, title: String, description: String?): EventRecord?
}