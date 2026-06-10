package me.blade.ktorweb.pages

import io.ktor.http.HttpStatusCode
import io.ktor.server.html.respondHtml
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.backgroundColor
import kotlinx.css.backgroundImage
import kotlinx.css.display
import kotlinx.css.flexDirection
import kotlinx.css.fontFamily
import kotlinx.css.height
import kotlinx.css.margin
import kotlinx.css.minHeight
import kotlinx.css.padding
import kotlinx.css.pct
import kotlinx.css.properties.GradientSideOrCorner
import kotlinx.css.properties.linearGradient
import kotlinx.css.px
import kotlinx.css.vh
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.title
import kotlinx.html.unsafe
import me.blade.ktorweb.KtorWeb
import me.blade.ktorweb.confs.RoutingContext
import me.blade.ktorweb.database.obj.UserEntry
import me.blade.ktorweb.prefabs.color
import me.blade.ktorweb.prefabs.css
import me.blade.ktorweb.prefabs.margin
import me.blade.ktorweb.prefabs.navbar
import me.blade.ktorweb.prefabs.padding
import me.blade.ktorweb.prefabs.pageWrapper
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

abstract class _Page {
    var displayName: String = "Unknown"; protected set
    var priority = 0; protected set

    var route: Array<String> = arrayOf()

    var minShowGroup: UserEntry.PermissionGroup = UserEntry.PermissionGroup.GUEST; protected set
    var minAccessGroup: UserEntry.PermissionGroup = UserEntry.PermissionGroup.GUEST; protected set
    var location: Location = Location.NAVBAR; protected set

    protected suspend inline fun RoutingContext.respondWrapped(
        status: HttpStatusCode = HttpStatusCode.OK,
        noinline block: FlowContent.() -> Unit
    ) {
        call.respondHtml(status) {
            wrapper(this@respondWrapped) {
                block()
            }
        }
    }

    protected fun HTML.wrapper(context: RoutingContext, block: FlowContent.() -> Unit) {
        head {
            title("ЮГ Опт - $displayName")
        }

        body {
            css {
                fontFamily = "Arial, sans-serif"
                margin = 0.px.margin()
                padding = 0.px.padding()

                height = 100.pct
                display = Display.flex
                flexDirection = FlexDirection.column
                minHeight = 100.vh

                backgroundColor = KtorWeb.colorPalette.background
            }

            navbar(this@_Page, context.user)
            pageWrapper {
                block(this@body)
            }
        }
    }

    suspend fun build(context: RoutingContext) =
        context.builder()

    protected abstract suspend fun RoutingContext.builder()

    enum class Location {
        NAVBAR,
        PROFILE
    }

    val baseRoute get() = route.first()
}