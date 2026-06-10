package me.blade.ktorweb.pages.order

import kotlinx.serialization.Serializable
import me.blade.ktorweb.KtorWeb
import me.blade.ktorweb.database.obj.UserEntry

@Serializable
data class PublishedOrder(
    val userLogin: String,
    val items: List<PublishedItem>,
    var status: Status = Status.QUEUED,
    var creationTimeStamp: Long,
    var processTimeStamp: Long? = null,
    var completionTimeStamp: Long? = null
) {
    val xml by lazy {
        val xmlBuilder = StringBuilder()
        xmlBuilder.appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        xmlBuilder.appendLine("<order>")

        items.forEach { item ->
            val itemEntry = KtorWeb.items.getItemByHash(item.itemHash) ?: return@forEach
            val itemName = itemEntry.name
            val count = item.itemCount

            xmlBuilder.appendLine("  <item>")
            xmlBuilder.appendLine("    <name>${escapeXml(itemName)}</name>")
            xmlBuilder.appendLine("    <count>$count</count>")
            xmlBuilder.appendLine("  </item>")
        }

        xmlBuilder.appendLine("</order>")
        xmlBuilder.toString()
    }

    private fun escapeXml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    fun nextStage() {
        when(status) {
            Status.QUEUED -> {
                processTimeStamp = System.currentTimeMillis()
            }
            Status.IN_PROGRESS -> {
                completionTimeStamp = System.currentTimeMillis()
            }
            Status.DONE -> {
                KtorWeb.orders.instances.remove(this)
            }
        }

        status = Status.entries[(status.ordinal + 1).coerceAtMost(Status.entries.lastIndex)]
    }

    val hash get() = "$userLogin/$creationTimeStamp/${items.hashCode()}".hashCode()

    @Serializable
    data class PublishedItem(
        val itemHash: Int,
        val itemCount: Double,
    ) {
        val itemEntry by lazy { KtorWeb.items.getItemByHash(itemHash)!! }
    }

    enum class Status(val displayName: String) {
        QUEUED("В очереди"),
        IN_PROGRESS("Обрабатывается"),
        DONE("Выполнен"),
    }

    val user get() = KtorWeb.users.findUserByLogin(userLogin)!!

    companion object {
        fun fromCart(user: UserEntry, cart: SessionCart) =
            PublishedOrder(user.login, cart.items.mapNotNull {
                PublishedItem(it.key, it.value)
            }, creationTimeStamp = System.currentTimeMillis())
    }
}