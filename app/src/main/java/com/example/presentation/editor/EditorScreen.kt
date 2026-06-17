package com.example.presentation.editor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.speech.RecognizerIntent
import android.app.Activity
import android.content.Intent
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Mic
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.presentation.editor.components.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.domain.model.PdfExporter
import com.example.domain.model.DocxExporter
import android.widget.Toast

import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restore
import androidx.compose.animation.AnimatedVisibility
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    onNavigateBack: () -> Unit,
    documentId: String,
    viewModel: EditorViewModel = viewModel(
        factory = EditorViewModel.provideFactory(documentId)
    )
) {
    val document by viewModel.document.collectAsState()
    val content by viewModel.content.collectAsState()
    val isFormatting by viewModel.isFormatting.collectAsState()
    val cursors by viewModel.cursors.collectAsState()
    val versions by viewModel.versions.collectAsState()

    var showVersions by remember { mutableStateOf(false) }
    var showExportPreview by remember { mutableStateOf(false) }

    var contentState by remember {
        mutableStateOf(androidx.compose.ui.text.input.TextFieldValue(content))
    }

    LaunchedEffect(content) {
        if (contentState.text != content) {
            contentState = contentState.copy(text = content)
        }
    }

    var showFindReplace by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText: String? = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            if (spokenText != null) {
                val selection = contentState.selection
                val text = contentState.text
                val newText = text.substring(0, selection.start) + spokenText + " " + text.substring(selection.end)
                contentState = contentState.copy(
                    text = newText,
                    selection = androidx.compose.ui.text.TextRange(selection.start + spokenText.length + 1)
                )
                viewModel.updateContent(newText)
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(document?.name ?: "Editor") },
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.updateContent(contentState.text)
                            viewModel.saveDocument()
                            onNavigateBack()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { 
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            }
                            try {
                                speechRecognizerLauncher.launch(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Speech recognition not available", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(Icons.Filled.Mic, "Dictate")
                        }
                        if (isFormatting) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(12.dp).size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconButton(onClick = { 
                                viewModel.updateContent(contentState.text)
                                viewModel.formatDocumentText() 
                            }) {
                                Icon(Icons.Filled.AutoFixHigh, "Auto Format")
                            }
                        }
                        IconButton(onClick = { showFindReplace = !showFindReplace }) {
                            Icon(Icons.Filled.Search, "Search")
                        }
                        IconButton(onClick = { showVersions = !showVersions }) {
                            Icon(Icons.Filled.History, "History")
                        }
                        IconButton(onClick = { /* Check spell logic */ }) {
                            Icon(Icons.Filled.FactCheck, "Spell Check")
                        }
                        IconButton(onClick = { 
                            viewModel.updateContent(contentState.text)
                            viewModel.saveDocument() 
                        }) {
                            Icon(Icons.Filled.Save, "Save")
                        }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Filled.MoreVert, "More")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Export Document") },
                                    onClick = {
                                        showMenu = false
                                        showExportPreview = true
                                    }
                                )
                            }
                        }
                    }
                )
                RibbonToolbar(
                    onBold = {
                        val selection = contentState.selection
                        val text = contentState.text
                        if (!selection.collapsed) {
                            val start = selection.min
                            val end = selection.max
                            if (start >= 2 && end <= text.length - 2 && text.substring(start - 2, start) == "**" && text.substring(end, end + 2) == "**") {
                                // Remove bold
                                val newText = text.substring(0, start - 2) + text.substring(start, end) + text.substring(end + 2)
                                contentState = contentState.copy(
                                    text = newText,
                                    selection = androidx.compose.ui.text.TextRange(start - 2, end - 2)
                                )
                            } else {
                                // Add bold
                                val newText = text.substring(0, start) + "**" + text.substring(start, end) + "**" + text.substring(end)
                                contentState = contentState.copy(
                                    text = newText,
                                    selection = androidx.compose.ui.text.TextRange(start + 2, end + 2)
                                )
                            }
                        } else {
                            val newText = text.substring(0, selection.start) + "****" + text.substring(selection.start)
                            contentState = contentState.copy(
                                text = newText,
                                selection = androidx.compose.ui.text.TextRange(selection.start + 2, selection.start + 2)
                            )
                        }
                        viewModel.updateContent(contentState.text)
                        viewModel.updateCursorPosition(contentState.selection.end)
                    },
                    onItalic = {
                        val selection = contentState.selection
                        val text = contentState.text
                        if (!selection.collapsed) {
                            val start = selection.min
                            val end = selection.max
                            if (start >= 1 && end <= text.length - 1 && text.substring(start - 1, start) == "_" && text.substring(end, end + 1) == "_") {
                                // Remove italic
                                val newText = text.substring(0, start - 1) + text.substring(start, end) + text.substring(end + 1)
                                contentState = contentState.copy(
                                    text = newText,
                                    selection = androidx.compose.ui.text.TextRange(start - 1, end - 1)
                                )
                            } else {
                                // Add italic
                                val newText = text.substring(0, start) + "_" + text.substring(start, end) + "_" + text.substring(end)
                                contentState = contentState.copy(
                                    text = newText,
                                    selection = androidx.compose.ui.text.TextRange(start + 1, end + 1)
                                )
                            }
                        } else {
                            val newText = text.substring(0, selection.start) + "__" + text.substring(selection.start)
                            contentState = contentState.copy(
                                text = newText,
                                selection = androidx.compose.ui.text.TextRange(selection.start + 1, selection.start + 1)
                            )
                        }
                        viewModel.updateContent(contentState.text)
                        viewModel.updateCursorPosition(contentState.selection.end)
                    },
                    onList = {
                        val selection = contentState.selection
                        val text = contentState.text
                        val lastNewlineIndex = text.substring(0, selection.start).lastIndexOf('\n')
                        val insertPos = if (lastNewlineIndex == -1) 0 else lastNewlineIndex + 1
                        
                        val newText = text.substring(0, insertPos) + "- " + text.substring(insertPos)
                        contentState = contentState.copy(
                            text = newText,
                            selection = androidx.compose.ui.text.TextRange(selection.start + 2, selection.end + 2)
                        )
                        viewModel.updateContent(contentState.text)
                    }
                )
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val textTrimmed = contentState.text.trim()
                    val wordCount = if (textTrimmed.isEmpty()) 0 else textTrimmed.split(Regex("\\s+")).size
                    val charCount = contentState.text.length
                    Text(
                        text = "$wordCount words",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "$charCount characters",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column {
                    OutlinedTextField(
                        value = contentState,
                        onValueChange = { 
                            contentState = it
                            viewModel.updateContent(it.text) 
                            viewModel.updateCursorPosition(it.selection.end)
                        },
                        visualTransformation = MarkdownVisualTransformation(searchQuery, cursors),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        placeholder = { Text("Start typing...") }
                    )
                }

                FindReplacePanel(
                    isVisible = showFindReplace,
                    onClose = { 
                        showFindReplace = false 
                        searchQuery = ""
                    },
                    onFind = { query -> searchQuery = query },
                    onReplace = { find, replace ->
                        if (find.isNotEmpty() && contentState.text.contains(find, ignoreCase = true)) {
                            val text = contentState.text
                            val index = text.indexOf(find, ignoreCase = true)
                            if (index != -1) {
                                val newText = text.substring(0, index) + replace + text.substring(index + find.length)
                                contentState = contentState.copy(text = newText)
                                viewModel.updateContent(newText)
                            }
                        }
                    },
                    onReplaceAll = { find, replace ->
                        if (find.isNotEmpty()) {
                            val newText = contentState.text.replace(Regex(Regex.escape(find), RegexOption.IGNORE_CASE), replace)
                            contentState = contentState.copy(text = newText)
                            viewModel.updateContent(newText)
                        }
                    }
                )
            }
            
            AnimatedVisibility(visible = showVersions) {
                Surface(
                    modifier = Modifier.width(300.dp).fillMaxHeight(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 4.dp
                ) {
                    Column {
                        Text(
                            "Version History", 
                            style = MaterialTheme.typography.titleMedium, 
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(
                            onClick = { viewModel.createSaveState() }, 
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        ) {
                            Text("Save Current State")
                        }
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(versions) { version ->
                                ListItem(
                                    headlineContent = { Text(SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(version.timestamp))) },
                                    supportingContent = { Text("Length: ${version.content.length} chars") },
                                    trailingContent = {
                                        IconButton(onClick = { viewModel.revertToVersion(version) }) {
                                            Icon(Icons.Filled.Restore, contentDescription = "Revert")
                                        }
                                    }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }

    if (showExportPreview) {
        Dialog(onDismissRequest = { showExportPreview = false }) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Export Preview", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color.White)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        val previewText = MarkdownVisualTransformation().filter(AnnotatedString(contentState.text)).text
                        Text(text = previewText, color = Color.Black)
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { showExportPreview = false }) {
                            Text("Cancel")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            val uri = PdfExporter().exportToPdf(context, contentState.text, document?.name ?: "Document")
                            if (uri != null) Toast.makeText(context, "Saved PDF to Downloads", Toast.LENGTH_SHORT).show()
                            showExportPreview = false
                        }) {
                            Text("Export PDF")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            val uri = DocxExporter().exportToDocx(context, contentState.text, document?.name ?: "Document")
                            if (uri != null) Toast.makeText(context, "Saved DOCX to Downloads", Toast.LENGTH_SHORT).show()
                            showExportPreview = false
                        }) {
                            Text("Export DOCX")
                        }
                    }
                }
            }
        }
    }
}
