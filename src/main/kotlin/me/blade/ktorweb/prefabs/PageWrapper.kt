package me.blade.ktorweb.prefabs

import kotlinx.css.*
import kotlinx.css.properties.BoxShadow
import kotlinx.css.properties.BoxShadowInset
import kotlinx.css.properties.GradientSideOrCorner
import kotlinx.css.properties.linearGradient
import kotlinx.html.*

fun BODY.pageWrapper(block: FlowContent.() -> Unit) {
    div {
        css {
            flex = Flex(1)
            margin = 15.px.margin()
            padding = 15.px.padding()

            backgroundColor = "#FFFFFF00".color()
            borderRadius = 5.px

            /*boxShadow += BoxShadowInset(
                "#00000033".color(),
                0.px, 0.px,
                5.px, 1.px
            )*/
        }

        block()
    }
}