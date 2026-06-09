package me.blade.ktorweb.database.impl

import me.blade.ktorweb.database.JsonDatabase
import me.blade.ktorweb.pages.order.PublishedOrder
import java.util.concurrent.CopyOnWriteArrayList

class OrderDatabase : JsonDatabase<PublishedOrder>(
    "orders.json",
    PublishedOrder.serializer()
) {
    override val instances = CopyOnWriteArrayList<PublishedOrder>()
}