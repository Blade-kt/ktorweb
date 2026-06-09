package me.blade.ktorweb.database.impl

import io.ktor.server.auth.UserIdPrincipal
import me.blade.ktorweb.database.IUserDatabase
import me.blade.ktorweb.database.obj.UserEntry
import java.util.concurrent.ConcurrentHashMap

// TEMP: SERVER LIFETIME ONLY
class UserDatabase : IUserDatabase {
    private val userMap = ConcurrentHashMap<String, UserEntry>()
    override val instances get() = userMap.values

    override suspend fun fetchAll() {
        userMap.clear()
        append(UserEntry.create(
            UserEntry.PermissionGroup.ADMIN,
            "АДМИНИСТРАТОР",
            "admin",
            "admin"
        ))

        append(UserEntry.create(
            UserEntry.PermissionGroup.MANAGER,
            "Иванов Иван Иванович",
            "manager",
            "manager"
        ))
    }

    override suspend fun storeAll() {

    }

    override suspend fun append(instance: UserEntry) {
        userMap[instance.login] = instance
    }

    override fun findUserByLogin(login: String) =
        userMap[login]

    override fun findUserByPrincipalOrGuest(principal: UserIdPrincipal?) =
        principal?.let {
            findUserByLogin(it.name)
        } ?: UserEntry.GUEST
}