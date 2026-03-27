package org.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlin.system.*

val RequestTimingPlugin = createApplicationPlugin(name = "RequestTimingPlugin") {
    onCall { call ->
        val start = System.nanoTime()
        try {
            
        } finally {
            val ms = (System.nanoTime() - start) / 1_000_000
            call.response.headers.append("X-Response-Time-ms", ms.toString())
        }
    }
}