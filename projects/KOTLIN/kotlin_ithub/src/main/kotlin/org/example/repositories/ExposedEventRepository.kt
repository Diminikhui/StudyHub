package org.example.repositories

import org.example.db.DatabaseFactory
import org.example.db.Events
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.dao.id.EntityID
import org.example.db.Users

class ExposedEventRepository(
    private val db: DatabaseFactory
) : EventRepository {

    override suspend fun getAll(): List<EventRecord> = db.dbQuery {
        Events.selectAll()
            .orderBy(Events.id, SortOrder.DESC)
            .map(::toEventRecord)
    }

    override suspend fun create(ownerId: Long, title: String, description: String?): EventRecord = db.dbQuery {
        val id = Events.insertAndGetId {
            it[Events.ownerId] = EntityID(ownerId, Users)
            it[Events.title] = title
            it[Events.description] = description
        }.value

        // вернуть созданную запись
        Events.selectAll()
            .where { Events.id eq id }
            .limit(1)
            .map(::toEventRecord)
            .single()
    }

    override suspend fun findById(id: Long): EventRecord? = db.dbQuery {
        Events
            .select { Events.id eq id }
            .limit(1)
            .map { row ->
                EventRecord(
                    id = row[Events.id].value,
                    title = row[Events.title],
                    description = row[Events.description],
                    ownerId = row[Events.ownerId].value
                )
            }
            .singleOrNull()
    }

    override suspend fun update(id: Long, title: String, description: String?): EventRecord? = db.dbQuery {
        val updated = Events.update({ Events.id eq id }) {
            it[Events.title] = title
            it[Events.description] = description
        }
        if (updated == 0) return@dbQuery null

        Events
            .select { Events.id eq id }
            .limit(1)
            .map { row ->
                EventRecord(
                    id = row[Events.id].value,
                    title = row[Events.title],
                    description = row[Events.description],
                    ownerId = row[Events.ownerId].value
                )
            }
            .singleOrNull()
    }

    override suspend fun deleteIfOwner(eventId: Long, ownerId: Long): Boolean = db.dbQuery {
        val deleted = Events.deleteWhere {
            (Events.id eq eventId) and (Events.ownerId eq EntityID(ownerId, Users))
        }
        deleted > 0
    }

    private fun toEventRecord(row: ResultRow): EventRecord =
        EventRecord(
            id = row[Events.id].value,
            title = row[Events.title],
            description = row[Events.description],
            ownerId = row[Events.ownerId].value
        )
}