package com.example.presentation.editor.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun SelectionOverlay(
    bounds: Rect,
    rotation: Float,
    onBoundsChange: (Rect) -> Unit,
    onRotationChange: (Float) -> Unit
) {
    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                // Simplified moving logic
                onBoundsChange(bounds.translate(dragAmount.x, dragAmount.y))
            }
        }) {
        // Draw border
        drawRect(
            color = Color.Blue,
            topLeft = bounds.topLeft,
            size = bounds.size,
            style = Stroke(width = 4f)
        )
        
        // Draw 8 handles
        val handlePoints = listOf(
            bounds.topLeft,
            bounds.topCenter,
            bounds.topRight,
            bounds.centerLeft,
            bounds.centerRight,
            bounds.bottomLeft,
            bounds.bottomCenter,
            bounds.bottomRight
        )
        
        handlePoints.forEach { point ->
            drawCircle(
                color = Color.White,
                radius = 12f,
                center = point
            )
            drawCircle(
                color = Color.Blue,
                radius = 12f,
                center = point,
                style = Stroke(width = 2f)
            )
        }
        
        // Draw rotate handle
        val rotateHandlePos = Offset(bounds.topCenter.x, bounds.topCenter.y - 60f)
        drawLine(
            color = Color.Blue,
            start = bounds.topCenter,
            end = rotateHandlePos,
            strokeWidth = 2f
        )
        drawCircle(
            color = Color.Green,
            radius = 12f,
            center = rotateHandlePos
        )
    }
}
