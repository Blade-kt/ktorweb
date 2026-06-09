package me.blade.ktorweb.prefabs

import kotlinx.css.Align
import kotlinx.css.BorderStyle
import kotlinx.css.Color
import kotlinx.css.CssBuilder
import kotlinx.css.Cursor
import kotlinx.css.FontWeight
import kotlinx.css.LinearDimension
import kotlinx.css.Margin
import kotlinx.css.Padding
import kotlinx.css.alignItems
import kotlinx.css.backgroundColor
import kotlinx.css.borderRadius
import kotlinx.css.borderStyle
import kotlinx.css.boxShadow
import kotlinx.css.color
import kotlinx.css.cursor
import kotlinx.css.fontSize
import kotlinx.css.fontWeight
import kotlinx.css.padding
import kotlinx.css.properties.BoxShadow
import kotlinx.css.properties.BoxShadowInset
import kotlinx.css.properties.Transition
import kotlinx.css.properties.s
import kotlinx.css.px
import kotlinx.css.rem
import kotlinx.css.tr
import kotlinx.css.transition
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.style
import me.blade.ktorweb.KtorWeb

fun CommonAttributeGroupFacade.css(block: CssBuilder.() -> Unit) {
    style = CssBuilder().apply(block).toString()
}

fun CssBuilder.buttonStyles(active: Boolean = false, shadow: Boolean = false) {
    fontSize = 0.8.rem
    fontWeight = FontWeight.bold
    padding = Padding(0.5.rem, 1.rem)
    borderStyle = BorderStyle.none
    borderRadius = 100.px
    transition += Transition(duration = 0.3.s)
    backgroundColor = KtorWeb.colorPalette.havbar2
    cursor = Cursor.pointer
    alignItems = Align.center

    if (active) {
        backgroundColor = KtorWeb.colorPalette.havbar1.desaturate(50).darken(20)
        color = Color.white

        boxShadow += BoxShadowInset(
            Color.black.withAlpha(0.1),
            (-1).px, (-1).px,
            5.px, 1.px
        )
    }

    if (shadow) {
        boxShadow += BoxShadow(
            Color.black.withAlpha(0.2),
            2.px, 2.px,
            5.px, 1.px
        )
    }
}

fun String.color() =
    Color(this)

fun LinearDimension.margin() =
    Margin(this)

fun LinearDimension.padding() =
    Padding(this)