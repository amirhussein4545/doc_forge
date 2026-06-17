package com.example.presentation.editor.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FindReplacePanel(
    isVisible: Boolean,
    onClose: () -> Unit,
    onFind: (String) -> Unit,
    onReplace: (String, String) -> Unit,
    onReplaceAll: (String, String) -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                var findText by remember { mutableStateOf("") }
                var replaceText by remember { mutableStateOf("") }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = findText,
                        onValueChange = { findText = it; onFind(it) },
                        label = { Text("Find") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(onClick = { /* Find Prev */ }) { Icon(Icons.Default.KeyboardArrowUp, null) }
                    IconButton(onClick = { /* Find Next */ }) { Icon(Icons.Default.KeyboardArrowDown, null) }
                    IconButton(onClick = onClose) { Icon(Icons.Default.Close, null) }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = replaceText,
                        onValueChange = { replaceText = it },
                        label = { Text("Replace with") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onReplace(findText, replaceText) }) {
                        Text("Replace")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedButton(onClick = { onReplaceAll(findText, replaceText) }) {
                        Text("Replace All")
                    }
                }
            }
        }
    }
}
