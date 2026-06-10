package me.blade.ktorweb.pages.order

import io.ktor.http.encodeURLParameter
import io.ktor.server.response.respondRedirect
import kotlinx.css.*
import kotlinx.css.properties.AspectRatio
import kotlinx.css.properties.BoxShadowInset
import kotlinx.css.properties.TextDecoration
import kotlinx.html.*
import me.blade.ktorweb.KtorWeb
import me.blade.ktorweb.confs.RoutingContext
import me.blade.ktorweb.database.obj.ItemEntry
import me.blade.ktorweb.database.obj.UserEntry
import me.blade.ktorweb.pages._Page
import me.blade.ktorweb.prefabs.buttonStyles
import me.blade.ktorweb.prefabs.color
import me.blade.ktorweb.prefabs.css
import me.blade.ktorweb.prefabs.padding
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.text.toIntOrNull

object Order : _Page() {
    init {
        displayName = "Оформить Заказ"
        priority = 2
        route = arrayOf("order")
    }

    private fun FlowContent.searchForm(searchQuery: String) {
        form {
            action = "/order"
            method = FormMethod.get

            css {
                display = Display.flex
                gap = 10.px
                alignItems = Align.center
            }

            input {
                css {
                    flexGrow = 1.0
                    padding = Padding(8.px)
                    borderRadius = 4.px
                    borderStyle = BorderStyle.solid
                    borderWidth = 1.px
                    borderColor = "#ccc".color()
                }

                type = InputType.text
                name = "search"
                value = searchQuery
                placeholder = "Поиск товаров..."
            }

            button {
                css {
                    padding = Padding(8.px, 16.px)
                    borderRadius = 4.px
                    cursor = Cursor.pointer
                    backgroundColor = "#2c3e50".color()
                    color = Color.white
                    borderStyle = BorderStyle.none
                }

                type = ButtonType.submit
                +"Найти"
            }

            if (searchQuery.isNotEmpty()) {
                a {
                    href = "/order"
                    css {
                        padding = Padding(8.px, 16.px)
                        borderRadius = 4.px
                        textDecoration = TextDecoration.none
                        color = "#2c3e50".color()
                        borderStyle = BorderStyle.solid
                        borderWidth = 1.px
                        borderColor = "#2c3e50".color()
                    }
                    +"Сбросить"
                }
            }
        }
    }

    private fun FlowContent.itemBlock(searchQuery: String) {
        div {
            css {
                fontWeight = FontWeight.bold
                width = 60.pct
                marginRight = 10.px
                borderRadius = 5.px
                padding = 10.px.padding()
                backgroundColor = KtorWeb.colorPalette.havbar2
            }
            +"Товар:"

            div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                }

                val items = KtorWeb.items.instances

                val filteredItems = if (searchQuery.isNotBlank()) {
                    items.filter { item ->
                        searchQuery.split(" ").all { word ->
                            item.name.contains(word, ignoreCase = true)
                        }

                    }
                } else {
                    items
                }

                if (filteredItems.isEmpty()) {
                    div {
                        css {
                            padding = Padding(20.px)
                            textAlign = TextAlign.center
                            color = "#666".color()
                        }
                        +"Товары не найдены"
                    }
                } else {
                    val maxItems = 30
                    filteredItems.take(maxItems).forEachIndexed { index, item ->
                        itemCard(searchQuery, item, index == 0)
                    }
                    if (filteredItems.size > maxItems) {
                        +"Ещё ${filteredItems.size - 30}..."
                    }
                }
            }
        }
    }

    private fun FlowContent.itemCard(searchQuery: String, item: ItemEntry, isFirst: Boolean = false) {
        div {
            css {
                display = Display.flex
                alignItems = Align.center
                flexDirection = FlexDirection.row
                justifyContent = JustifyContent.spaceBetween

                margin = Margin(vertical = 5.px)
                padding = Padding(8.px)
                buttonStyles(searchQuery.isNotBlank() && isFirst, shadow = true)
            }

            div {
                css {
                    flexGrow = 1.0
                }
                +item.name
            }

            form {
                action = "/order"
                method = FormMethod.get

                css {
                    display = Display.flex
                    height = LinearDimension.auto
                    alignItems = Align.center
                    margin = Margin(0.px)
                    padding = Padding(0.px)
                    gap = 5.px
                }

                +"Остаток: ${item.displayCount} ${item.displayUnit}"

                // Preserve search query when adding to cart
                input {
                    type = InputType.hidden
                    name = "search"
                    value = searchQuery
                }

                input {
                    type = InputType.hidden
                    name = "add"
                    value = "${item.hash}"
                }

                input {
                    css {
                        display = Display.flex
                        height = LinearDimension.auto
                        width = 50.px
                        marginRight = 5.px
                    }

                    type = InputType.number
                    name = "qty"
                    value = "1"
                    step = "0.01"
                    //max = item.count.toString()
                }

                button {
                    css {
                        aspectRatio = AspectRatio(1)
                        alignItems = Align.center
                        cursor = Cursor.pointer

                        borderStyle = BorderStyle.solid
                        borderWidth = 1.px
                        borderRadius = 100.pct

                        color = KtorWeb.colorPalette.textPrimary
                        borderColor = KtorWeb.colorPalette.havbar1
                        backgroundColor = KtorWeb.colorPalette.background
                    }

                    type = ButtonType.submit
                    +"+"
                }
            }
        }
    }

    private fun FlowContent.buylistBlock(searchQuery: String, cart: SessionCart) {
        div {
            css {
                fontWeight = FontWeight.bold
                width = 40.pct
                padding = 10.px.padding()

                borderRadius = 5.px
                backgroundColor = KtorWeb.colorPalette.havbar2
            }
            +"Корзина: "

            div {
                css {
                    fontWeight = FontWeight.normal
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    gap = 5.px
                }

                if (cart.items.isEmpty()) {
                    + """
                        "Пока тут пусто.
                         Начните поиск нужного товара в форме 'Поиск товаров...',
                         выберите товар, введите количество
                         и используйте кнопку '+' для добавления в корзину "
                    """.trimIndent()
                } else {
                    div {
                        css {
                            display = Display.flex
                            flexDirection = FlexDirection.row
                            justifyContent = JustifyContent.spaceBetween
                            padding = Padding(8.px)
                            buttonStyles()
                            boxShadow += BoxShadowInset(
                                Color.black.withAlpha(0.1),
                                0.px, 0.px,
                                10.px, 1.px
                            )
                        }

                        div {
                            val count = cart.items.size
                            val shit = when (count.toString().last().digitToIntOrNull()) {
                                1 -> "позиция"
                                2, 3, 4  -> "позиции"
                                else -> "позиций"
                            }
                            +"Итого: ${cart.items.size} $shit, ${cart.items.values.sumOf { it }} ед."
                        }
                        div {
                            var sum = 0.0
                            cart.items.forEach {
                                val item = KtorWeb.items.getItemByHash(it.key) ?: return@forEach
                                sum += item.price * it.value
                            }
                            sum *= 100
                            sum = sum.roundToInt().toDouble() / 100.0
                            +"$sum руб"
                        }

                        form {
                            action = "/order"
                            method = FormMethod.get

                            input {
                                type = InputType.hidden
                                name = "order"
                            }

                            button {
                                type = ButtonType.submit

                                css {
                                    buttonStyles(true)
                                }

                                +"Завершить заказ"
                            }
                        }
                    }

                    cart.items.forEach { (itemHash, count) ->
                        div {
                            buylistCard(searchQuery, itemHash, count)
                        }
                    }
                }
            }
        }
    }

    private fun HtmlBlockTag.buylistCard(searchQuery: String, itemHash: Int, count: Double) {
        css {
            display = Display.flex
            flexDirection = FlexDirection.row
            justifyContent = JustifyContent.spaceBetween

            //margin = Margin(vertical = 5.px)
            padding = Padding(8.px)
            borderRadius = 4.px
            alignItems = Align.center

            buttonStyles(shadow = true)
        }

        div {
            +(KtorWeb.items.getItemByHash(itemHash)?.name ?: "Unknown")
        }

        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
                justifyContent = JustifyContent.end
            }

            form {
                action = "/order"
                method = FormMethod.get
                css {
                    margin = Margin(0.px)
                    marginRight = 5.px
                    borderStyle = BorderStyle.none
                    borderRadius = 5.px
                }

                input {
                    type = InputType.hidden
                    name = "setCount"
                    value = itemHash.toString()
                }

                input {
                    css {
                        width = 70.px
                    }
                    type = InputType.number
                    name = "qty"
                    value = count.toString()
                }
            }

            form {
                action = "/order"
                method = FormMethod.get
                css {
                    margin = Margin(0.px)
                }

                input {
                    type = InputType.hidden
                    name = "remove"
                    value = itemHash.toString()
                }

                input {
                    type = InputType.hidden
                    name = "search"
                    value = searchQuery
                }

                button {
                    type = ButtonType.submit
                    css {
                        cursor = Cursor.pointer
                        borderStyle = BorderStyle.solid
                        borderWidth = 2.px
                        borderRadius = 100.px

                        color = KtorWeb.colorPalette.havbar1
                        borderColor = KtorWeb.colorPalette.havbar1
                        backgroundColor = KtorWeb.colorPalette.background
                    }
                    +"Удалить"
                }
            }
        }
    }

    private suspend fun RoutingContext.setCount(searchQuery: String, cart: SessionCart, itemId: Int, count: Double) {
        val item = KtorWeb.items.getItemByHash(itemId) ?: return
        cart.set(item.hash, min(count, item.availableCount))

        val encodedSearch = searchQuery.encodeURLParameter()
        call.respondRedirect("/order?search=$encodedSearch")
        return
    }

    override suspend fun RoutingContext.builder() {
        val cart = user.tempCart

        val addItem = call.request.queryParameters["add"]
        val removeItem = call.request.queryParameters["remove"]
        val searchQuery = call.request.queryParameters["search"] ?: ""
        val setCount = call.request.queryParameters["setCount"]

        val order = call.request.queryParameters["order"]

        order?.let {
            KtorWeb.orders.append(PublishedOrder.fromCart(user, cart))
            cart.items.clear()

            call.respondRedirect("/order")
            return
        }

        addItem?.let {
            val itemId = addItem.toIntOrNull() ?: return@let
            val count = call.request.queryParameters["qty"]?.toDoubleOrNull() ?: return@let

            setCount(searchQuery, cart, itemId, (cart.items[itemId] ?: 0.0) + count)
            return
        }

        setCount?.toIntOrNull()?.let { itemId ->
            val count = call.request.queryParameters["qty"]?.toDoubleOrNull() ?: return@let
            if (count <= 0) {
                cart.items.remove(itemId)
                val encodedSearch = searchQuery.encodeURLParameter()
                call.respondRedirect("/order?search=$encodedSearch")
                return
            }
            setCount(searchQuery, cart, itemId, count)
            return
        }

        removeItem?.let {
            val itemId = removeItem.toIntOrNull() ?: return@let
            cart.items.remove(itemId)

            val encodedSearch = searchQuery.encodeURLParameter()
            call.respondRedirect("/order?search=$encodedSearch")
            return
        }

        respondWrapped {
            if (user.permissionGroup == UserEntry.PermissionGroup.GUEST) {
                h1 { +"Для создания заказа войдите в профиль" }
                return@respondWrapped
            }

            div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    gap = 20.px
                }

                searchForm(searchQuery)

                div {
                    css {
                        display = Display.flex
                        flexDirection = FlexDirection.row
                        justifyContent = JustifyContent.spaceBetween
                        gap = 20.px
                    }

                    itemBlock(searchQuery)
                    buylistBlock(searchQuery, cart)
                }
            }
        }
    }
}