package me.blade.ktorweb.pages

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import kotlinx.css.Color
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.JustifyContent
import kotlinx.css.Padding
import kotlinx.css.backgroundColor
import kotlinx.css.borderRadius
import kotlinx.css.boxShadow
import kotlinx.css.display
import kotlinx.css.flexDirection
import kotlinx.css.justifyContent
import kotlinx.css.margin
import kotlinx.css.padding
import kotlinx.css.properties.BoxShadowInset
import kotlinx.css.px
import kotlinx.html.*
import me.blade.ktorweb.KtorWeb
import me.blade.ktorweb.confs.RoutingContext
import me.blade.ktorweb.database.obj.UserEntry
import me.blade.ktorweb.pages.order.PublishedOrder.Status
import me.blade.ktorweb.prefabs.buttonStyles
import me.blade.ktorweb.prefabs.css
import me.blade.ktorweb.prefabs.margin
import me.blade.ktorweb.prefabs.padding
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

object OrderProcessing : _Page() {
    init {
        displayName = "Обработать заказы"
        priority = 3

        route = arrayOf("process_orders")
        minShowGroup = UserEntry.PermissionGroup.MANAGER
        minAccessGroup = UserEntry.PermissionGroup.MANAGER
    }

    override suspend fun RoutingContext.builder() {
        val nextStage = call.request.queryParameters["nextStage"]
        val orderHash = nextStage?.toIntOrNull()
        orderHash?.let { hash ->
            val order = KtorWeb.orders.instances.firstOrNull { it.hash == hash } ?: return@let
            order.nextStage()
        }

        val getXML = call.request.queryParameters["getXML"]
        getXML?.let {
            val order = KtorWeb.orders.instances.firstOrNull() ?: return@let
            val xmlContent = order.xml

            call.response.headers.append("Content-Type", "application/xml")
            call.response.headers.append("Content-Disposition", "attachment; filename=\"order_${order.hash}.xml\"")
            call.respondText(xmlContent, ContentType.Application.Xml)
        }

        respondWrapped {
            if (user.permissionGroup.ordinal < UserEntry.PermissionGroup.MANAGER.ordinal) {
                h1 {
                    +"Недоступно"
                }

                return@respondWrapped
            }

            h1 {
                +"Заказы: "
            }

            fun epochToReadableDate(epochMillis: Long?): String {
                if (epochMillis == null) return "Нет"
                val instant = java.time.Instant.ofEpochMilli(epochMillis)
                val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                return dateTime.format(formatter)
            }

            KtorWeb.orders.instances.forEach { order ->
                div {
                    css {
                        display = Display.flex
                        flexDirection = FlexDirection.column
                        margin = 5.px.margin()
                        padding = 8.px.padding()
                        backgroundColor = KtorWeb.colorPalette.havbar2
                        borderRadius = 10.px
                        boxShadow += BoxShadowInset(
                            Color.black.withAlpha(0.1),
                            0.px, 0.px,
                            10.px, 1.px
                        )
                    }

                    val receiver = order.user.nickname
                    val sum = order.items.toList().sumOf { publishedItem ->
                        publishedItem.itemEntry.price * publishedItem.itemCount
                    }.let { sum ->
                        sum.times(100).roundToInt().toDouble() / 100.0
                    }

                    val creationDate = epochToReadableDate(order.creationTimeStamp)
                    val processDate = epochToReadableDate(order.processTimeStamp)
                    val completionDate = epochToReadableDate(order.completionTimeStamp)

                    div {
                        +"$receiver от $creationDate на сумму ${sum}руб"
                    }

                    div {
                        css {
                            display = Display.flex
                            flexDirection = FlexDirection.row
                        }
                        +"Статус: ${order.status.displayName}"
                    }

                    div { +"Принято в обработку: $processDate" }
                    div { +"Отмечено к выгрузке: $completionDate" }

                    br()

                    div {
                        css {
                            display = Display.flex
                            flexDirection = FlexDirection.row
                        }

                        form {
                            action = "/process_orders"
                            method = FormMethod.get

                            input {
                                type = InputType.hidden
                                name = "getXML"
                            }

                            button {
                                type = ButtonType.submit
                                +"Получить XML"
                            }
                        }
                        form {
                            action = "/process_orders"
                            method = FormMethod.get

                            input {
                                type = InputType.hidden
                                name = "nextStage"
                                value = "${order.hash}"
                            }

                            button {
                                type = ButtonType.submit

                                +when(order.status) {
                                    Status.QUEUED -> "Отметить как обрабатываемый"
                                    Status.IN_PROGRESS -> "Отметить как выполненное"
                                    Status.DONE -> "Удалить из сайта"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}