package me.blade.ktorweb.database.obj

import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
data class ItemEntry(
    val name: String,
    val price: Double,
    val availableCount: Double,
    val unit: String
) {
    val hash = name.hashCode()
    val displayCount = run {
        if (availableCount.roundToInt().toDouble() == availableCount) {
            return@run availableCount.roundToInt().toString()
        }

        return@run availableCount.toString()
    }
    val displayUnit = run {
        /*if (unit == "шт") return@run unit
        return@run "шт [$unit]"*/
        return@run unit
    }
}