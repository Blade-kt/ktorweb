package me.blade.ktorweb.database.impl

import me.blade.ktorweb.database.IItemDatabase
import me.blade.ktorweb.database.obj.ItemEntry
import org.jdom2.input.SAXBuilder
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.roundToInt

// TEMP: SERVER LIFETIME ONLY
class ItemDatabase(
    private val xml: String
) : IItemDatabase {
    override val instances = CopyOnWriteArrayList<ItemEntry>()
    private val hashMap = hashMapOf<Int, ItemEntry>()

    override suspend fun fetchAll() {
        val result = build()

        instances.clear()
        hashMap.clear()

        instances.addAll(result)
        result.forEach { hashMap[it.hash] = it }
    }

    override suspend fun storeAll() {

    }

    private fun build() = mutableListOf<ItemEntry>().also { items ->
        val xmlStream = ClassLoader.getSystemResourceAsStream(xml) ?: return@also

        val builder = SAXBuilder()
        val doc = builder.build(xmlStream)
        val root = doc.rootElement

        val workSheet = root.children.firstOrNull {
            it.name == "Worksheet"
        } ?: throw IllegalStateException("Unable to find worksheet base")

        val table = workSheet.children.firstOrNull {
            it.name == "Table"
        } ?: throw IllegalStateException("Unable to find base table")

        val rows = table.children.filter { it.name == "Row" }

        rows.forEach { row ->
            val cellDataList = row.children.filter {
                it.name == "Cell"
            }.mapNotNull { cell ->
                cell.children.firstOrNull {
                    it.name == "Data"
                }
            }

            val name = cellDataList.getOrNull(0)?.text ?: return@forEach

            val unit = cellDataList.getOrNull(1)?.text ?: return@forEach

            val priceText = cellDataList.getOrNull(2)?.text ?: return@forEach
            val price = priceText.toDouble().times(100.0).roundToInt().div(100.0)

            val countText = cellDataList.getOrNull(3)?.text ?: return@forEach
            val count = countText.toDouble().times(100.0).roundToInt().div(100.0)

            if (count <= 0) return@forEach
            if (price <= 0.0) return@forEach

            items += ItemEntry(name, price, count, unit)
        }
    }

    override suspend fun append(instance: ItemEntry) {
        throw UnsupportedOperationException("Card list DB is Read-Only")
    }

    override fun getItemByHash(hash: Int) =
        hashMap[hash]
}