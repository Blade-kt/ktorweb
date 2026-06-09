package me.blade.ktorweb.confs

import io.ktor.server.application.ApplicationCall
import me.blade.ktorweb.database.obj.UserEntry

class RoutingContext(
    val call: ApplicationCall,
    val user: UserEntry
)