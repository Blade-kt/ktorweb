package me.blade.ktorweb.prefabs

import kotlinx.css.Color

class ColorPalette(
    val background: Color,
    val havbar1: Color,
    val havbar2: Color,
    val textPrimary: Color,
    val textSecondary: Color,
) {
    companion object {
        val TEST = ColorPalette(
            "#ffffff".color(),
            "#87cefa".color(),
            "#f0f8ff".color(),
            "#1e90ff".color(),
            "#00bfff".color()
        )
    }
}