package me.blade.ktorweb.pages

import io.ktor.http.HttpStatusCode
import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.button
import kotlinx.html.form
import kotlinx.html.h1
import me.blade.ktorweb.confs.RoutingContext
import me.blade.ktorweb.database.obj.UserEntry

object Logout : _Page() {
    init {
        displayName = Profile.displayName

        route = arrayOf("logout")
        minShowGroup = UserEntry.PermissionGroup._HIDDEN
        minAccessGroup = UserEntry.PermissionGroup.REGISTERED
    }

    override suspend fun RoutingContext.builder() {
         respondWrapped(HttpStatusCode.Unauthorized) {
            h1 { +"Вы вышли с аккаунта" }

            form(action = General.baseRoute, method = FormMethod.get) {
                button {
                    type = ButtonType.submit
                    +"На главную"
                }
            }
        }
    }
}