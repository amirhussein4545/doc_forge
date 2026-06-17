package com.example.data.repository

import com.example.domain.model.Document
import com.example.domain.model.DocumentFormat
import com.example.domain.repository.DocumentRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseDocumentRepositoryImpl(
    private val firestore: FirebaseFirestore
) : DocumentRepository {

    private val collection = firestore.collection("documents")

    override fun getAllDocuments(): Flow<List<Document>> = callbackFlow {
        val subscription = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val documents = snapshot.documents.mapNotNull { doc ->
                    try {
                        Document(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            format = DocumentFormat.valueOf(doc.getString("format") ?: "MD"),
                            lastModified = doc.getLong("lastModified") ?: 0L,
                            fileSize = doc.getLong("fileSize") ?: 0L,
                            isStarred = doc.getBoolean("isStarred") ?: false,
                            content = doc.getString("content") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(documents)
            }
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun getDocumentById(id: String): Document? {
        return try {
            val doc = collection.document(id).get().await()
            if (doc.exists()) {
                Document(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    format = DocumentFormat.valueOf(doc.getString("format") ?: "MD"),
                    lastModified = doc.getLong("lastModified") ?: 0L,
                    fileSize = doc.getLong("fileSize") ?: 0L,
                    isStarred = doc.getBoolean("isStarred") ?: false,
                    content = doc.getString("content") ?: ""
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveDocument(document: Document) {
        val data = mapOf(
            "name" to document.name,
            "format" to document.format.name,
            "lastModified" to document.lastModified,
            "fileSize" to document.fileSize,
            "isStarred" to document.isStarred,
            "content" to document.content
        )
        // Do not await() so the app remains responsive while offline.
        collection.document(document.id).set(data, SetOptions.merge())
    }

    override suspend fun deleteDocument(document: Document) {
        // Do not await() so the app remains responsive while offline.
        collection.document(document.id).delete()
    }
}
