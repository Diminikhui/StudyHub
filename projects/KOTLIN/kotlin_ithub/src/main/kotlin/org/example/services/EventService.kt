package org.example.services

import org.example.repositories.EventRecord
import org.example.repositories.EventRepository

class EventService(private val repo: EventRepository) {

    suspend fun listAll(): List<EventRecord> = repo.getAll()

    suspend fun create(ownerId: Long, title: String, description: String?): EventRecord =
        repo.create(ownerId = ownerId, title = title, description = description)

    suspend fun findById(id: Long): EventRecord? = repo.findById(id)

    suspend fun updateIfOwner(id: Long, ownerId: Long, title: String, description: String?): EventRecord? {
        val existing = repo.findById(id) ?: return null
        if (existing.ownerId != ownerId) return null
        return repo.update(id = id, title = title, description = description)
    }

    suspend fun deleteIfOwner(eventId: Long, ownerId: Long): Boolean =
        repo.deleteIfOwner(eventId = eventId, ownerId = ownerId)
}