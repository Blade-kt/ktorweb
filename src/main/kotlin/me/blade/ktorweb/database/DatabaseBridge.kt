package me.blade.ktorweb.database

interface DatabaseBridge<EntryType> {
    val instances: MutableCollection<EntryType>

    suspend fun fetchAll()
    suspend fun storeAll()

    suspend fun append(instance: EntryType)
}