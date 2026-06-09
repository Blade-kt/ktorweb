package me.blade.ktorweb.database

import io.ktor.server.auth.UserIdPrincipal
import me.blade.ktorweb.database.obj.UserEntry

interface IUserDatabase : DatabaseBridge<UserEntry> {
    fun findUserByLogin(login: String): UserEntry?
    fun findUserByPrincipalOrGuest(principal: UserIdPrincipal?): UserEntry
}