package me.blade.ktorweb.confs

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.serialization.KotlinxSessionSerializer
import kotlinx.serialization.json.Json
import me.blade.ktorweb.pages.order.SessionCart

fun Application.configureSessions() {
    install(Sessions) {
        cookie<SessionCart>("SHOPPING_CART") {
            cookie.path = "/"
            cookie.httpOnly = true
            cookie.maxAgeInSeconds = 3600 // 1 hour

            serializer = KotlinxSessionSerializer(Json {
                encodeDefaults = true
                ignoreUnknownKeys = true
            })
        }
    }
}