package me.blade.ktorweb.pages

import kotlinx.html.*
import me.blade.ktorweb.KtorWeb
import me.blade.ktorweb.confs.RoutingContext
import me.blade.ktorweb.database.obj.UserEntry
import kotlin.math.roundToInt

object OrderProcessing : _Page() {
    init {
        displayName = "Обработать заказы"
        priority = 3

        route = arrayOf("process_orders")
        minShowGroup = UserEntry.PermissionGroup.MANAGER
        //location = Location.PROFILE
    }

    override suspend fun RoutingContext.builder() {
        respondWrapped {
            if (user.permissionGroup.ordinal < UserEntry.PermissionGroup.MANAGER.ordinal) {
                h1 {
                    +"Недоступно"
                }

                return@respondWrapped
            }

            h1 {
                +"Заказы: "
            }

            KtorWeb.orders.instances.forEach {
                h3 {
                    +"${it.user.nickname}: ${it.items.toList().sumOf { it.first.price * it.second }.let { sum ->
                        sum.times(100).roundToInt().toDouble() / 100.0
                    }}руб"
                }
            }
        }
    }
}