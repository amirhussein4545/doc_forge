package com.example.presentation.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CropScreen(
    imagePath: String,
    onCropConfirmed: () -> Unit,
    onCancel: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Mock image area
        Box(
            modifier = Modifier
                .fillMaxSize(0.8f)
                .align(Alignment.Center)
                .background(Color.DarkGray)
        ) {
            Text("Crop Area Overlay", color = Color.White, modifier = Modifier.align(Alignment.Center))
        }

        // Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onCancel) {
                Icon(Icons.Default.Close, "Cancel", tint = Color.White)
            }
            Row {
                TextButton(onClick = { /* 1:1 */ }) { Text("1:1", color = Color.White) }
                TextButton(onClick = { /* 4:3 */ }) { Text("4:3", color = Color.White) }
                TextButton(onClick = { /* 16:9 */ }) { Text("16:9", color = Color.White) }
            }
            IconButton(onClick = onCropConfirmed) {
                Icon(Icons.Default.Check, "Confirm", tint = Color.Green)
            }
        }
    }
}
