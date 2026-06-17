package com.example.data.repository

import com.example.data.local.DocumentDao
import com.example.data.local.DocumentEntity
import com.example.domain.model.Document
import com.example.domain.model.DocumentFormat
import com.example.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DocumentRepositoryImpl(
    private val dao: DocumentDao
) : DocumentRepository {
    override fun getAllDocuments(): Flow<List<Document>> {
        return dao.getAllDocuments().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getDocumentById(id: String): Document? {
        return dao.getDocumentById(id)?.toDomain()
    }

    override suspend fun saveDocument(document: Document) {
        dao.insertDocument(document.toEntity())
    }

    override suspend fun deleteDocument(document: Document) {
        dao.deleteDocument(document.toEntity())
    }

    private fun DocumentEntity.toDomain(): Document {
        return Document(
            id = id,
            name = name,
            format = DocumentFormat.valueOf(format),
            lastModified = lastModified,
            fileSize = fileSize,
            isStarred = isStarred,
            content = content
        )
    }

    private fun Document.toEntity(): DocumentEntity {
        return DocumentEntity(
            id = id,
            name = name,
            format = format.name,
            lastModified = lastModified,
            fileSize = fileSize,
            isStarred = isStarred,
            content = content
        )
    }
}
