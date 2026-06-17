package com.example.presentation.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@Composable
fun MiniToolbar(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    Surface(
        modifier = modifier.shadow(8.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.FormatBold, "Bold")
            }
            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.FormatItalic, "Italic")
            }
            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.FormatUnderlined, "Underline")
            }
            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.FormatColorText, "Color")
            }
            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.AutoFixHigh, "AI Rewrite")
            }
        }
    }
}
