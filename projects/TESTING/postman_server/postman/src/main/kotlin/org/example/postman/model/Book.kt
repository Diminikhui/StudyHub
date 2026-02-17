package org.example.postman.model

data class Book(
    val id: Long,
    val userId: Long,
    var title: String,
    var author: String,
    var year: Int
)