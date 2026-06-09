package me.blade.ktorweb.database

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import me.blade.ktorweb.pages.order.PublishedOrder
import java.io.File

abstract class JsonDatabase<T>(
    private val filePath: String,
    serializer: KSerializer<T>
) : DatabaseBridge<T> {
    private val json: Json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        allowStructuredMapKeys = true
    }

    private val dataFile = File(filePath)
    private val listSerializer = ListSerializer(serializer)

    // Only update in memory and defer saving to json
    override suspend fun append(instance: T) {
        instances.add(instance)
    }

    override suspend fun fetchAll() {
        println("[JSON] Fetching $filePath")

        if (!dataFile.exists()) {
            instances.clear()
            return
        }

        try {
            val jsonString = dataFile.readText()
            if (jsonString.isBlank()) {
                instances.clear()
                return
            }

            val list = json.decodeFromString(listSerializer, jsonString)
            instances.clear()
            instances.addAll(list)
        } catch (e: Exception) {
            println("Error fetching from JSON: ${e.message}")
            instances.clear()
        }
    }

    override suspend fun storeAll() {
        println("[JSON] Storing $filePath")
        println("[JSON] Location ${dataFile.absolutePath}")

        try {
            val jsonString = json.encodeToString(listSerializer, instances.toMutableList())
            dataFile.writeText(jsonString)
        } catch (e: Exception) {
            println("Error storing to JSON: ${e.message}")
        }
    }
}