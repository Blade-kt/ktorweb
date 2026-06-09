package me.blade.ktorweb.confs

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import me.blade.ktorweb.KtorWeb

fun Application.configureAuth() {
    install(Authentication) {
        basic("user-auth") {
            realm = "ACCESS"

            validate { credentials ->
                KtorWeb.users.findUserByLogin(credentials.name)?.let { user ->
                    if (!user.checkPW(credentials.password)) {
                        return@let null
                    }

                    UserIdPrincipal(credentials.name)
                } ?: run {
                    null
                }
            }
        }
    }
}