package com.example.presentation.editor.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ColorPickerWheel(
    modifier: Modifier = Modifier,
    initialColor: Color,
    onColorChanged: (Color) -> Unit
) {
    var hue by remember { mutableStateOf(0f) }
    var saturation by remember { mutableStateOf(1f) }
    val value = 1f // keep lightness max for wheel

    Canvas(modifier = modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectDragGestures { change, _ ->
                val center = Offset(size.width / 2f, size.height / 2f)
                val pos = change.position
                val dx = pos.x - center.x
                val dy = pos.y - center.y
                val radius = size.width / 2f
                val dist = Math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
                
                saturation = (dist / radius).coerceIn(0f, 1f)
                hue = ((atan2(dy.toDouble(), dx.toDouble()) * 180 / PI).toFloat() + 360) % 360
                
                onColorChanged(Color.hsv(hue, saturation, value))
            }
        }) {
        val radius = size.width / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        
        // Simplified Hue ring drawing
        for (i in 0 until 360 step 5) {
            val color = Color.hsv(i.toFloat(), 1f, 1f)
            drawArc(
                color = color,
                startAngle = i.toFloat(),
                sweepAngle = 6f,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = 20f)
            )
        }
        
        // Draw selection knob
        val knobAngle = hue * PI / 180
        val knobDist = saturation * radius
        val knobX = center.x + knobDist * cos(knobAngle).toFloat()
        val knobY = center.y + knobDist * sin(knobAngle).toFloat()
        
        drawCircle(
            color = Color.White,
            radius = 16f,
            center = Offset(knobX, knobY)
        )
        drawCircle(
            color = Color.Black,
            radius = 16f,
            center = Offset(knobX, knobY),
            style = Stroke(width = 2f)
        )
    }
}
