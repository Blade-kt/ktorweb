package me.blade.ktorweb.prefabs

import kotlinx.css.*
import kotlinx.css.properties.BoxShadow
import kotlinx.css.properties.BoxShadowInset
import kotlinx.css.properties.GradientSideOrCorner
import kotlinx.css.properties.TextDecoration
import kotlinx.css.properties.Transition
import kotlinx.css.properties.linearGradient
import kotlinx.css.properties.s
import kotlinx.html.*
import me.blade.ktorweb.KtorWeb
import me.blade.ktorweb.pages._Page
import me.blade.ktorweb.database.obj.UserEntry

fun BODY.navbar(currentPage: _Page, user: UserEntry) {
    nav {
        css {
            padding = 5.px.padding()
        }

        div {
            css {
                backgroundImage = linearGradient(sideOrCorner = GradientSideOrCorner.ToRight, false) {
                    colorStop(KtorWeb.colorPalette.havbar2)
                    colorStop(KtorWeb.colorPalette.havbar2)
                }

                boxShadow += BoxShadow(
                    "#00000033".color(),
                    0.px, 0.px,
                    4.px, 1.px
                )

                borderRadius = 100.px

                display = Display.flex
                padding = Padding(horizontal = 20.px, vertical = 10.px)
                justifyContent = JustifyContent.spaceBetween
                alignItems = Align.center
            }

            div(classes = "logo") {
                a(href = "/") {
                    css {
                        color = KtorWeb.colorPalette.textPrimary
                        fontSize = 1.5.rem
                        fontWeight = FontWeight.bold
                        textDecoration = TextDecoration.none
                        transition += Transition("opacity", 0.3.s)

                        hover {
                            opacity = 0.8
                        }
                    }

                    +"ЮГ Опт"
                }
            }
            div {
                css {
                    display = Display.flex
                    gap = 2.rem
                }

                KtorWeb.pages.forEach { page ->
                    if (page.location != _Page.Location.NAVBAR) return@forEach
                    if (!user.canAccess(page)) return@forEach
                    pageAccessButton(page, page == currentPage)
                }
            }
        }
    }
}

fun FlowContent.pageAccessButton(page: _Page, highlight: Boolean = false) {
    button {
        css {
            buttonStyles(highlight)

            boxShadow += BoxShadow(
                Color.black.withAlpha(0.1),
                0.px, 0.px,
                3.px, 1.px
            )
        }

        onClick = "window.location.href = '/${page.baseRoute}'"
        type = ButtonType.submit

        if (page.minShowGroup.ordinal >= UserEntry.PermissionGroup.REGISTERED.ordinal) {
            +"(*) "
        }

        +page.displayName
    }
}