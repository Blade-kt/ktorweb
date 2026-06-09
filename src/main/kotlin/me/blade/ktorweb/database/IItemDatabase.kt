package me.blade.ktorweb.database

import me.blade.ktorweb.database.obj.ItemEntry

interface IItemDatabase : DatabaseBridge<ItemEntry> {
    fun getItemByHash(hash: Int): ItemEntry?
}