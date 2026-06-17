package com.example.domain.repository

import com.example.domain.model.Document
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun getAllDocuments(): Flow<List<Document>>
    suspend fun getDocumentById(id: String): Document?
    suspend fun saveDocument(document: Document)
    suspend fun deleteDocument(document: Document)
}
