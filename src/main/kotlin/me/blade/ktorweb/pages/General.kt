package me.blade.ktorweb.pages

import kotlinx.html.h1
import me.blade.ktorweb.confs.RoutingContext

object General : _Page() {
    init {
        displayName = "Главная"
        priority = 0

        route = arrayOf("general", "/")
    }

    override suspend fun RoutingContext.builder() {
        respondWrapped {
            h1 {
                +"Главная страница"
            }
        }
    }
}