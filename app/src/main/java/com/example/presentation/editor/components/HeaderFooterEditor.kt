package com.example.presentation.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HeaderFooterEditor(
    isHeader: Boolean,
    content: String,
    onContentChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
            .background(Color(0xFFF9F9F9))
            .padding(16.dp)
    ) {
        Text(
            text = if (isHeader) "Header Area" else "Footer Area",
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        // Simplified content area
        Text(text = content.ifEmpty { "Double tap to edit..." })
    }
}
