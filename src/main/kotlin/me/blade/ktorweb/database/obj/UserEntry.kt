package me.blade.ktorweb.database.obj

import kotlinx.serialization.Serializable
import me.blade.ktorweb.pages._Page
import me.blade.ktorweb.pages.order.SessionCart
import java.security.MessageDigest

@Serializable
open class UserEntry private constructor(
    val nickname: String,

    val login: String,
    private val passwordHash: String,

    val permissionGroup: PermissionGroup,
) {
    val tempCart = SessionCart()

    fun checkPW(password: String) =
        password.hashPW() == passwordHash

    object ADMIN : UserEntry("Администратор", "admin", "admin", PermissionGroup.ADMIN)
    object GUEST : UserEntry("Гость", "", "", PermissionGroup.GUEST)

    companion object {
        private fun String.hashPW(): String {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(this.toByteArray())
            return digest.toHexString()
        }

        fun create(permissionGroup: PermissionGroup, nickname: String, login: String, password: String): UserEntry  =
            UserEntry(nickname, login,password.hashPW(), permissionGroup)
    }

    fun canAccess(page: _Page) =
        permissionGroup.ordinal >= page.minShowGroup.ordinal

    enum class PermissionGroup(val displayName: String) {
        GUEST("Гость"),
        REGISTERED("Пользователь"),
        MANAGER("Оператор"),
        ADMIN("Администратор"),

        _HIDDEN("СКРЫТО")
    }
}