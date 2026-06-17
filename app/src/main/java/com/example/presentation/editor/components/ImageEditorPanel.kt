package com.example.presentation.editor.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domain.model.ImageElement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageEditorPanel(
    image: ImageElement,
    onClose: () -> Unit,
    onUpdate: (ImageElement) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Adjust", "Style", "Layout", "Alt Text")

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
                when (selectedTab) {
                    0 -> AdjustTab(image, onUpdate)
                    1 -> StyleTab(image, onUpdate)
                    2 -> LayoutTab(image, onUpdate)
                    3 -> AltTextTab(image, onUpdate)
                }
            }
        }
    }
}

@Composable
private fun AdjustTab(image: ImageElement, onUpdate: (ImageElement) -> Unit) {
    Column {
        Text("Brightness")
        Slider(
            value = image.brightness,
            onValueChange = { onUpdate(image.copy(brightness = it)) },
            valueRange = -100f..100f
        )
        Text("Contrast")
        Slider(
            value = image.contrast,
            onValueChange = { onUpdate(image.copy(contrast = it)) },
            valueRange = -100f..100f
        )
        // Others simplified for space
    }
}

@Composable
private fun StyleTab(image: ImageElement, onUpdate: (ImageElement) -> Unit) {
    Column {
        Text("Opacity")
        Slider(
            value = image.opacity,
            onValueChange = { onUpdate(image.copy(opacity = it)) },
            valueRange = 0f..1f
        )
    }
}

@Composable
private fun LayoutTab(image: ImageElement, onUpdate: (ImageElement) -> Unit) {
    Column {
        Text("X Position: ${image.x.toInt()}")
        Text("Y Position: ${image.y.toInt()}")
        // Additional settings would go here
    }
}

@Composable
private fun AltTextTab(image: ImageElement, onUpdate: (ImageElement) -> Unit) {
    Column {
        OutlinedTextField(
            value = image.altTextTitle,
            onValueChange = { onUpdate(image.copy(altTextTitle = it)) },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = image.altTextDescription,
            onValueChange = { onUpdate(image.copy(altTextDescription = it)) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
    }
}
