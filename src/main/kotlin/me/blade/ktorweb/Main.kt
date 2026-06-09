package me.blade.ktorweb

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.blade.ktorweb.confs.configureAuth
import me.blade.ktorweb.confs.configureRouting
import me.blade.ktorweb.confs.configureSessions
import me.blade.ktorweb.database.DatabaseBridge
import me.blade.ktorweb.database.IItemDatabase
import me.blade.ktorweb.database.IUserDatabase
import me.blade.ktorweb.database.obj.ItemEntry
import me.blade.ktorweb.database.impl.ItemDatabase
import me.blade.ktorweb.database.impl.OrderDatabase
import me.blade.ktorweb.database.impl.UserDatabase
import me.blade.ktorweb.pages._Page
import me.blade.ktorweb.prefabs.ColorPalette
import org.reflections.Reflections
import kotlin.properties.Delegates
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object KtorWeb {
    var colorPalette = ColorPalette.TEST

    var items by Delegates.notNull<IItemDatabase>()
    var users by Delegates.notNull<IUserDatabase>()
    var orders by Delegates.notNull<OrderDatabase>()
    val databases by lazy {
        setOf(items, users, orders)
    }

    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val pages = Reflections("me.blade.ktorweb.pages")
        .getSubTypesOf(_Page::class.java)
        .mapNotNull {
            it.instance
        }.sortedBy {
            it.priority
        }.also {
            println("Found pages: ${it.joinToString { page -> page.displayName }}")
        }

    @Suppress("UNCHECKED_CAST")
    val <T> Class<out T>.instance
        get() = this.getDeclaredField("INSTANCE")[null] as T
}

fun main() {
    println("Loading bridges...")

    KtorWeb.items = ItemDatabase(xml = "items.xml")
    KtorWeb.users = UserDatabase()
    KtorWeb.orders = OrderDatabase()

    runBlocking {
        KtorWeb.databases.forEach {
            it.fetchAll()
        }
    }

    KtorWeb.scope.launch {
        while (true) {
            println("Saving databases")
            KtorWeb.databases.forEach {
                it.storeAll()
            }
            delay(5.seconds)
        }
    }

    println("Starting embedded server")
    embeddedServer(
        Netty,
        port = 8080,
        module = {
            configureSessions()
            configureAuth()
            configureRouting()
        }
    ).start(wait = true)


    println("Shutting down")
}