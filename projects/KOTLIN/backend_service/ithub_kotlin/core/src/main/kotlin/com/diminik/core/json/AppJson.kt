package com.diminik.core.json

import kotlinx.serialization.json.Json

object AppJson {
    val default: Json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }
}
