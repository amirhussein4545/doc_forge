package com.example.presentation.editor.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domain.model.ShapeElement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShapeEditorPanel(
    shape: ShapeElement,
    onClose: () -> Unit,
    onUpdate: (ShapeElement) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Fill", "Line", "Effects", "Text")

    ModalBottomSheet(onDismissRequest = onClose) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.6f)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Simplified UI:
                Text("Editing properties for: ${shape.type.name}")
                // Real implementation would have color pickers, stroke width sliders etc.
            }
        }
    }
}
