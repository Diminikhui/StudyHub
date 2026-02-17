package org.example.postman

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PostmanApplication

fun main(args: Array<String>) {
    runApplication<PostmanApplication>(*args)
}
