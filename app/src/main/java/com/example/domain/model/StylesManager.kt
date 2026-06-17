package com.example.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

data class ParagraphStyle(
    val name: String,
    val fontFamily: String = "SansSerif",
    val fontSize: Float = 12f,
    val fontWeight: FontWeight = FontWeight.Normal,
    val color: Color = Color.Black,
    val lineHeight: Float = 1.5f,
    val spaceBefore: Float = 0f,
    val spaceAfter: Float = 0f,
    val indent: Float = 0f,
    val keepWithNext: Boolean = false,
    val pageBreakBefore: Boolean = false
)

class StylesManager {
    val styles = mutableListOf(
        ParagraphStyle(name = "Normal"),
        ParagraphStyle(name = "Heading 1", fontSize = 24f, fontWeight = FontWeight.Bold),
        ParagraphStyle(name = "Heading 2", fontSize = 20f, fontWeight = FontWeight.Bold),
        ParagraphStyle(name = "Title", fontSize = 28f, fontWeight = FontWeight.Bold),
        ParagraphStyle(name = "Quote", color = Color.DarkGray, indent = 16f)
    )

    fun addStyle(style: ParagraphStyle) {
        styles.add(style)
    }

    fun getStyle(name: String): ParagraphStyle? {
        return styles.find { it.name == name }
    }
}
