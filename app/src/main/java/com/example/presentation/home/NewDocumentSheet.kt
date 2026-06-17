package com.example.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.automirrored.rounded.TextSnippet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.DocumentFormat

enum class DocumentTemplate(val displayName: String, val content: String) {
    BLANK("Blank Document", ""),
    RESUME("Resume", "# [Your Name]\n[Contact Information]\n\n## Objective\n\n\n## Experience\n\n\n## Education\n\n"),
    MEETING_NOTES("Meeting Notes", "# Meeting Name\n**Date:** \n**Attendees:** \n\n## Agenda\n- \n\n## Action Items\n- [ ] "),
    PROJECT_PROPOSAL("Project Proposal", "# Project Title\n## Executive Summary\n\n## Goals & Objectives\n\n## Timeline\n\n## Budget\n")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDocumentSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onCreate: (String, DocumentFormat, String) -> Unit
) {
    var docName by remember { mutableStateOf("") }
    var selectedFormat by remember { mutableStateOf<DocumentFormat?>(null) }
    var selectedTemplate by remember { mutableStateOf(DocumentTemplate.BLANK) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "Create New Document",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Format Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormatCard(
                    format = DocumentFormat.PDF,
                    icon = Icons.Rounded.PictureAsPdf,
                    color = MaterialTheme.colorScheme.error,
                    isSelected = selectedFormat == DocumentFormat.PDF,
                    onClick = { selectedFormat = DocumentFormat.PDF },
                    modifier = Modifier.weight(1f)
                )
                FormatCard(
                    format = DocumentFormat.DOCX,
                    icon = Icons.Rounded.Description,
                    color = MaterialTheme.colorScheme.primary,
                    isSelected = selectedFormat == DocumentFormat.DOCX,
                    onClick = { selectedFormat = DocumentFormat.DOCX },
                    modifier = Modifier.weight(1f)
                )
                FormatCard(
                    format = DocumentFormat.TXT,
                    icon = Icons.AutoMirrored.Rounded.TextSnippet,
                    color = MaterialTheme.colorScheme.tertiary,
                    isSelected = selectedFormat == DocumentFormat.TXT,
                    onClick = { selectedFormat = DocumentFormat.TXT },
                    modifier = Modifier.weight(1f)
                )
            }

            // Template Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Template",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(DocumentTemplate.values()) { template ->
                        FilterChip(
                            selected = selectedTemplate == template,
                            onClick = { selectedTemplate = template },
                            label = { Text(template.displayName) }
                        )
                    }
                }
            }

            // Name Input
            OutlinedTextField(
                value = docName,
                onValueChange = { docName = it },
                label = { Text("Document Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    if (selectedFormat != null && docName.isNotBlank()) {
                        onCreate(docName, selectedFormat!!, selectedTemplate.content)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = selectedFormat != null && docName.isNotBlank()
            ) {
                Text("Create Document")
            }
        }
    }
}

@Composable
fun FormatCard(
    format: DocumentFormat,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) color.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) BorderStroke(2.dp, color) else null,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = format.name,
                tint = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                format.name,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
