package com.example.presentation.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GradientEditor(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    angle: Float,
    onColorsChanged: (List<Color>) -> Unit,
    onAngleChanged: (Float) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text("Gradient Angle: ${angle.toInt()}°")
        Slider(
            value = angle,
            onValueChange = onAngleChanged,
            valueRange = 0f..360f
        )
        
        Row(
            modifier = Modifier.fillMaxWidth().height(40.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            colors.forEachIndexed { index, color ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(color)
                )
            }
        }
    }
}
