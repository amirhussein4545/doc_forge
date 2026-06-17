package com.example.domain.model

import androidx.compose.ui.graphics.Color

enum class ShapeType {
    Rectangle, RoundedRectangle, Circle, Ellipse, Triangle, Parallelogram,
    Star, Arrow, SpeechBubble
}

enum class FillType { None, Solid, LinearGradient, RadialGradient, Pattern }
enum class StrokeStyle { Solid, Dashed, Dotted, Double }

data class ShapeElement(
    val id: String,
    val type: ShapeType,
    val boundsX: Float,
    val boundsY: Float,
    val boundsWidth: Float,
    val boundsHeight: Float,
    val rotation: Float,
    val fillColor: Color,
    val fillType: FillType,
    val gradientColors: List<Color>,
    val gradientAngle: Float,
    val strokeColor: Color,
    val strokeWidth: Float,
    val strokeStyle: StrokeStyle,
    val cornerRadius: Float,
    val opacity: Float,
    val text: String,
    val zIndex: Int
)
