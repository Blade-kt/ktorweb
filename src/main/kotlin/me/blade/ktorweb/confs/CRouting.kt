package me.blade.ktorweb.confs

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import me.blade.ktorweb.KtorWeb
import me.blade.ktorweb.database.obj.UserEntry

fun Application.configureRouting() {
    routing {
        staticResources("/static", "static")

        KtorWeb.pages.forEach { page ->
            authenticate(
                "user-auth",
                optional = page.minAccessGroup == UserEntry.PermissionGroup.GUEST
            ) {
                page.route.forEach { route ->
                    get(route) {
                        val user = KtorWeb.users.findUserByPrincipalOrGuest(
                            call.principal<UserIdPrincipal>()
                        )

                        page.build(RoutingContext(call, user))
                    }
                }
            }
        }
    }
}

