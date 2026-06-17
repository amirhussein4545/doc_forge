package com.example.presentation.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.automirrored.filled.FormatIndentIncrease
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun RibbonToolbar(
    modifier: Modifier = Modifier,
    onBold: () -> Unit = {},
    onItalic: () -> Unit = {},
    onList: () -> Unit = {},
    onInsertImage: () -> Unit = {},
    onInsertTable: () -> Unit = {},
    onInsertShape: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Home", "Insert", "Draw", "Layout", "Review", "View")

    Column(modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 0.dp,
            divider = { Divider() }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            when (selectedTab) {
                0 -> HomeRibbonGroup(onBold, onItalic, onList)
                1 -> InsertRibbonGroup(onInsertImage, onInsertTable, onInsertShape)
                2 -> DrawRibbonGroup()
                3 -> LayoutRibbonGroup()
                4 -> ReviewRibbonGroup()
                5 -> ViewRibbonGroup()
                else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tools for ${tabs[selectedTab]}")
                }
            }
        }
    }
}

@Composable
private fun HomeRibbonGroup(onBold: () -> Unit, onItalic: () -> Unit, onList: () -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            RibbonButton(Icons.Default.FormatBold, "Bold", onBold)
            RibbonButton(Icons.Default.FormatItalic, "Italic", onItalic)
            RibbonButton(Icons.Default.FormatUnderlined, "Underline", {})
            VerticalDivider(modifier = Modifier.height(40.dp))
            RibbonButton(Icons.AutoMirrored.Filled.FormatListBulleted, "List", onList)
            VerticalDivider(modifier = Modifier.height(40.dp))
            RibbonButton(Icons.AutoMirrored.Filled.FormatAlignLeft, "Left", {})
            RibbonButton(Icons.Default.FormatAlignCenter, "Center", {})
            RibbonButton(Icons.AutoMirrored.Filled.FormatAlignRight, "Right", {})
            RibbonButton(Icons.Default.FormatAlignJustify, "Justify", {})
            VerticalDivider(modifier = Modifier.height(40.dp))
            Button(onClick = {}, modifier = Modifier.height(40.dp)) { Text("Styles") }
        }
    }
}

@Composable
private fun InsertRibbonGroup(onInsertImage: () -> Unit, onInsertTable: () -> Unit, onInsertShape: () -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            RibbonButton(Icons.Default.Image, "Image", onInsertImage)
            RibbonButton(Icons.Default.TableChart, "Table", onInsertTable)
            RibbonButton(Icons.Default.Category, "Shape", onInsertShape)
            VerticalDivider(modifier = Modifier.height(40.dp))
            RibbonButton(Icons.Default.Link, "Link", {})
            RibbonButton(Icons.Default.FormatListNumbered, "Header", {})
        }
    }
}

@Composable
private fun DrawRibbonGroup() {
    LazyRow(
        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            RibbonButton(Icons.Default.Edit, "Pen", {})
            RibbonButton(Icons.Default.Highlight, "Highlight", {})
            RibbonButton(Icons.Default.FormatClear, "Eraser", {})
            VerticalDivider(modifier = Modifier.height(40.dp))
            RibbonButton(Icons.Default.Category, "Shapes", {})
            VerticalDivider(modifier = Modifier.height(40.dp))
            RibbonButton(Icons.Default.TextFields, "Ink to Text", {})
        }
    }
}

@Composable
private fun LayoutRibbonGroup() {
    LazyRow(
        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            RibbonButton(Icons.Default.Margin, "Margins", {})
            RibbonButton(Icons.Default.ScreenRotation, "Orientation", {})
            RibbonButton(Icons.Default.PhotoSizeSelectActual, "Size", {})
            RibbonButton(Icons.Default.ViewColumn, "Columns", {})
            VerticalDivider(modifier = Modifier.height(40.dp))
            RibbonButton(Icons.AutoMirrored.Filled.FormatIndentIncrease, "Indent", {})
            RibbonButton(Icons.Default.FormatLineSpacing, "Spacing", {})
            VerticalDivider(modifier = Modifier.height(40.dp))
            RibbonButton(Icons.Default.Layers, "Arrange", {})
        }
    }
}

@Composable
private fun ReviewRibbonGroup() {
    LazyRow(
        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            RibbonButton(Icons.AutoMirrored.Filled.FactCheck, "Spelling", {})
            RibbonButton(Icons.Default.Language, "Language", {})
            VerticalDivider(modifier = Modifier.height(40.dp))
            RibbonButton(Icons.AutoMirrored.Filled.Comment, "Comment", {})
            VerticalDivider(modifier = Modifier.height(40.dp))
            RibbonButton(Icons.Default.TrackChanges, "Track", {})
            RibbonButton(Icons.Default.Compare, "Compare", {})
            RibbonButton(Icons.Default.Lock, "Protect", {})
        }
    }
}

@Composable
private fun ViewRibbonGroup() {
    LazyRow(
        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            RibbonButton(Icons.Default.Print, "Print Layout", {})
            RibbonButton(Icons.Default.Web, "Web Layout", {})
            RibbonButton(Icons.AutoMirrored.Filled.Article, "Read Mode", {})
            VerticalDivider(modifier = Modifier.height(40.dp))
            RibbonButton(Icons.Default.ZoomIn, "Zoom In", {})
            RibbonButton(Icons.Default.ZoomOut, "Zoom Out", {})
            RibbonButton(Icons.Default.Window, "New Window", {})
        }
    }
}

@Composable
private fun RibbonButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
            .width(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(24.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}
