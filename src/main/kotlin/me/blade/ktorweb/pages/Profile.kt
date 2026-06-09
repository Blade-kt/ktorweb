package me.blade.ktorweb.pages

import kotlinx.html.*
import me.blade.ktorweb.confs.RoutingContext
import me.blade.ktorweb.database.obj.UserEntry

object Profile : _Page() {
    init {
        displayName = "Мой Профиль"
        priority = 4

        route = arrayOf("profile")
        minAccessGroup = UserEntry.PermissionGroup.REGISTERED
    }

    override suspend fun RoutingContext.builder() {
        respondWrapped {
            if (user.permissionGroup == UserEntry.PermissionGroup.GUEST) {
                h1 {
                    +"Не авторизован"
                }
                return@respondWrapped
            }

            h2 {
                +"Профиль: ${user.nickname}"
            }

            h2 {
                +"Уровень доступа: ${user.permissionGroup.displayName}"
            }

            p {
                +"Вы вошли как: "
                b { +user.login }
            }

            form(action = "/logout", method = FormMethod.get) {
                button {
                    type = ButtonType.submit
                    +"Выйти из системы"
                }
            }
        }
    }
}