package org.example.postman.controller

import org.example.postman.dto.BookRequest
import org.example.postman.model.Book
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.NoSuchElementException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@RestController
@RequestMapping("/api/users/{userId}/books")
class BookController {

    private val idSeq = AtomicLong(1000)
    // userId -> (bookId -> Book)
    private val storage: MutableMap<Long, MutableMap<Long, Book>> = ConcurrentHashMap()

    @PostMapping
    fun create(@PathVariable userId: Long, @RequestBody req: BookRequest): ResponseEntity<Book> {
        validate(req)

        val id = idSeq.incrementAndGet()
        val book = Book(
            id = id,
            userId = userId,
            title = req.title!!.trim(),
            author = req.author!!.trim(),
            year = req.year!!
        )

        storage.computeIfAbsent(userId) { ConcurrentHashMap() }[id] = book
        return ResponseEntity.status(HttpStatus.CREATED).body(book)
    }

    @GetMapping
    fun list(@PathVariable userId: Long): List<Book> =
        storage[userId]?.values?.toList() ?: emptyList()

    @GetMapping("/{bookId}")
    fun get(@PathVariable userId: Long, @PathVariable bookId: Long): Book =
        storage[userId]?.get(bookId) ?: throw NoSuchElementException("Book not found")

    @PutMapping("/{bookId}")
    fun update(
        @PathVariable userId: Long,
        @PathVariable bookId: Long,
        @RequestBody req: BookRequest
    ): Book {
        validate(req)

        val book = storage[userId]?.get(bookId) ?: throw NoSuchElementException("Book not found")
        book.title = req.title!!.trim()
        book.author = req.author!!.trim()
        book.year = req.year!!
        return book
    }

    @DeleteMapping("/{bookId}")
    fun delete(@PathVariable userId: Long, @PathVariable bookId: Long): ResponseEntity<Void> {
        val removed = storage[userId]?.remove(bookId) ?: throw NoSuchElementException("Book not found")
        return ResponseEntity.noContent().build()
    }

    private fun validate(req: BookRequest) {
        if (req.title.isNullOrBlank()) throw IllegalArgumentException("title is required")
        if (req.author.isNullOrBlank()) throw IllegalArgumentException("author is required")
        val y = req.year ?: throw IllegalArgumentException("year is required")
        if (y < 0) throw IllegalArgumentException("year must be >= 0")
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to (ex.message ?: "Not found")))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to (ex.message ?: "Bad request")))
}