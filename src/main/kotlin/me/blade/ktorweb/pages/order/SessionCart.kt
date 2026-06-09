package me.blade.ktorweb.pages.order

import kotlinx.serialization.Serializable

@Serializable
data class SessionCart(
    val items: MutableMap<Int, Double> = mutableMapOf()
) {
    fun set(item: Int, count: Double) {
        items[item] = count
    }

    fun remove(item: Int, count: Double) {
        val updated = (items[item] ?: return) - count

        if (updated <= 0) {
            removeItem(item)
            return
        }

        items[item] = updated
    }

    fun removeItem(item: Int) {
        items.remove(item)
    }

    fun clear() {
        items.clear()
    }
}