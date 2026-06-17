package com.example.presentation.home

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.DocForgeApplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Document
import com.example.domain.model.DocumentFormat
import com.example.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class HomeViewModel(
    private val repository: DocumentRepository
) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as DocForgeApplication
                val repository = application.container.documentRepository
                return HomeViewModel(repository) as T
            }
        }
    }

    private val _documents = MutableStateFlow<List<Document>>(emptyList())
    val documents: StateFlow<List<Document>> = _documents.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllDocuments().collectLatest { docs ->
                _documents.value = docs
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun createDocument(name: String, format: DocumentFormat, content: String = "", onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val doc = Document(
                id = UUID.randomUUID().toString(),
                name = name.ifBlank { "Untitled Document" },
                format = format,
                lastModified = System.currentTimeMillis(),
                fileSize = 0L,
                content = content
            )
            repository.saveDocument(doc)
            onCreated(doc.id)
        }
    }

    fun deleteDocument(document: Document) {
        viewModelScope.launch {
            repository.deleteDocument(document)
        }
    }
}
