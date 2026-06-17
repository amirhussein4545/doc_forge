package com.example.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.TextSnippet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.model.Document
import com.example.domain.model.DocumentFormat
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToEditor: (String?) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val documents by viewModel.documents.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var isSearchExpanded by remember { mutableStateOf(false) }

    var showNewDocSheet by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.doc_pen_logo_1780477538104),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                        )
                        Text("DocForge", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    IconButton(onClick = { isSearchExpanded = !isSearchExpanded }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.secondary, CircleShape), contentAlignment = Alignment.Center) {
                        Text("JD", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNewDocSheet = true },
                icon = { Icon(Icons.Filled.Add, "New Document", tint = Color.White) },
                text = { Text("New Document", color = Color.White, fontWeight = FontWeight.SemiBold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Description, contentDescription = null) },
                    label = { Text("All Files", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.outline,
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.History, contentDescription = null) },
                    label = { Text("Recent", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Star, contentDescription = null) },
                    label = { Text("Starred", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedVisibility(visible = isSearchExpanded) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search documents...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    leadingIcon = { Icon(Icons.Filled.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                )
            }

            val filteredDocs = documents.filter { it.name.contains(searchQuery, ignoreCase = true) }.let { docs ->
                when (selectedTab) {
                    0 -> docs
                    1 -> docs.sortedByDescending { it.lastModified }
                    2 -> docs.filter { it.isStarred }
                    else -> docs
                }
            }

            if (filteredDocs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Rounded.Description,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Create your first document",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    items(filteredDocs) { doc ->
                        DocumentCard(
                            document = doc,
                            onClick = { onNavigateToEditor(doc.id) },
                            onDelete = { viewModel.deleteDocument(doc) }
                        )
                    }
                }
            }
        }
    }

    if (showNewDocSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()
        NewDocumentSheet(
            sheetState = sheetState,
            onDismiss = { showNewDocSheet = false },
            onCreate = { name, format, content ->
                viewModel.createDocument(name, format, content) { docId ->
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showNewDocSheet = false
                            onNavigateToEditor(docId)
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun DocumentCard(
    document: Document,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val isStarred = document.isStarred
    val containerColor = if (isStarred) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    val borderColor = if (isStarred) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline

    val (tagColor, tagBgColor) = when (document.format) {
        DocumentFormat.PDF -> com.example.ui.theme.PdfVariant to com.example.ui.theme.PdfContainer
        DocumentFormat.DOCX -> com.example.ui.theme.DocxVariant to com.example.ui.theme.DocxContainer
        DocumentFormat.TXT -> com.example.ui.theme.TxtVariant to com.example.ui.theme.TxtContainer
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(140.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isStarred) {
                // Top-right corner blue tint
                Box(
                    modifier = Modifier.align(Alignment.TopEnd).padding(end = 4.dp, top = 4.dp).size(24.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                )
            }
            Column(modifier = Modifier.padding(12.dp).fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        modifier = Modifier.background(tagBgColor, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(document.format.name, color = tagColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        if (isStarred) {
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                        }
                    }

                    Box(modifier = Modifier.offset(x = 12.dp, y = (-12).dp)) {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(text = { Text("Delete") }, onClick = { expanded = false; onDelete() })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = document.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.weight(1f))

                val dateStr = remember(document.lastModified) {
                    java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()).format(java.util.Date(document.lastModified))
                }

                Text(
                    text = "Modified $dateStr",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${if (document.fileSize > 0) document.fileSize / 1024 else 12} KB",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
