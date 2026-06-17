package com.example.presentation.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavController
import com.example.DocForgeApplication

import com.example.domain.model.Document
import com.example.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import androidx.lifecycle.createSavedStateHandle
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.UUID
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

data class CursorInfo(
    val userId: String,
    val position: Int,
    val colorHex: String = "#FF0000"
)

data class DocumentVersion(
    val id: String,
    val timestamp: Long,
    val content: String
)

class EditorViewModel(
    private val repository: DocumentRepository,
    private val docId: String
) : ViewModel() {
    private val userId = UUID.randomUUID().toString()
    private val userColor = listOf("#FF0000", "#00FF00", "#0000FF", "#FFA500", "#800080").random()

    private val rtdb = FirebaseDatabase.getInstance().getReference("documents/$docId/cursors")
    private val firestore = FirebaseFirestore.getInstance()
    private val versionCollection = firestore.collection("documents").document(docId).collection("versions")
    
    private val _cursors = MutableStateFlow<List<CursorInfo>>(emptyList())
    val cursors = _cursors.asStateFlow()

    private val _versions = MutableStateFlow<List<DocumentVersion>>(emptyList())
    val versions = _versions.asStateFlow()

    private val cursorListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val updatedCursors = mutableListOf<CursorInfo>()
            for (child in snapshot.children) {
                val uid = child.key ?: continue
                if (uid == userId) continue
                val pos = child.child("position").getValue(Int::class.java) ?: continue
                val color = child.child("colorHex").getValue(String::class.java) ?: "#FF0000"
                updatedCursors.add(CursorInfo(uid, pos, color))
            }
            _cursors.value = updatedCursors
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    companion object {
        fun provideFactory(
            documentId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as DocForgeApplication
                val repository = application.container.documentRepository
                return EditorViewModel(repository, documentId) as T
            }
        }
    }

    private val _document = MutableStateFlow<Document?>(null)
    val document = _document.asStateFlow()

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    private val _isFormatting = MutableStateFlow(false)
    val isFormatting = _isFormatting.asStateFlow()

    private val autoFormatter = com.example.data.remote.AutoFormatter()

    fun formatDocumentText() {
        if (_isFormatting.value) return
        _isFormatting.value = true
        viewModelScope.launch {
            try {
                val currentText = _content.value
                val formattedText = autoFormatter.formatDocument(currentText)
                _content.value = formattedText
            } finally {
                _isFormatting.value = false
            }
        }
    }

    fun updateCursorPosition(position: Int) {
        val cursorRef = rtdb.child(userId)
        val data = mapOf(
            "position" to position,
            "colorHex" to userColor
        )
        cursorRef.setValue(data)
        cursorRef.onDisconnect().removeValue()
    }

    init {
        rtdb.addValueEventListener(cursorListener)
        
        versionCollection.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val list = snapshot.documents.mapNotNull { doc ->
                    try {
                        DocumentVersion(
                            id = doc.id,
                            timestamp = doc.getLong("timestamp") ?: 0L,
                            content = doc.getString("content") ?: ""
                        )
                    } catch (ex: Exception) { null }
                }
                _versions.value = list
            }

        viewModelScope.launch {
            val doc = repository.getDocumentById(docId)
            _document.value = doc
            _content.value = doc?.content ?: ""
        }
        startAutoSave()
    }

    override fun onCleared() {
        super.onCleared()
        rtdb.removeEventListener(cursorListener)
        rtdb.child(userId).removeValue()
    }

    private fun startAutoSave() {
        viewModelScope.launch {
            while (isActive) {
                kotlinx.coroutines.delay(30000L)
                saveDocument()
            }
        }
    }

    fun updateContent(newContent: String) {
        _content.value = newContent
    }

    fun createSaveState() {
        val currentContent = _content.value
        val version = mapOf(
            "timestamp" to System.currentTimeMillis(),
            "content" to currentContent
        )
        versionCollection.add(version)
    }

    fun revertToVersion(version: DocumentVersion) {
        _content.value = version.content
        saveDocument()
    }

    fun saveDocument() {
        val currentDoc = _document.value ?: return
        viewModelScope.launch {
            val updatedDoc = currentDoc.copy(
                content = _content.value,
                lastModified = System.currentTimeMillis()
            )
            repository.saveDocument(updatedDoc)
            _document.value = updatedDoc
        }
    }
}
