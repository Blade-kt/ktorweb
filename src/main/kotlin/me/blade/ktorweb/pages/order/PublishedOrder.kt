package me.blade.ktorweb.pages.order

import kotlinx.serialization.Serializable
import me.blade.ktorweb.KtorWeb
import me.blade.ktorweb.database.obj.ItemEntry
import me.blade.ktorweb.database.obj.UserEntry

@Serializable
data class PublishedOrder(
    val userLogin: String,
    val items: Map<ItemEntry, Double> = mutableMapOf()
) {
    val user get() = KtorWeb.users.findUserByLogin(userLogin)!!

    companion object {
        fun fromCart(user: UserEntry, cart: SessionCart) =
            PublishedOrder(user.login, cart.items.mapNotNull {
                KtorWeb.items.getItemByHash(it.key)?.let { item ->
                    item to it.value
                }
            }.toMap())
    }
}