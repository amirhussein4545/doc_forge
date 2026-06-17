package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class DocumentFormat {
    PDF, DOCX, TXT
}

@Serializable
data class Document(
    val id: String,
    val name: String,
    val format: DocumentFormat,
    val lastModified: Long,
    val fileSize: Long,
    val isStarred: Boolean = false,
    val content: String = "" // Simplification for MVP
)

data class ExportOptions(
    val pdfQualityDpi: Int = 300,
    val docxCompatibilityMode: String = "Word 365",
    val txtEncoding: String = "UTF-8"
)
