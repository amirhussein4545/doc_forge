package com.example.domain.model

import androidx.compose.ui.text.SpanStyle

sealed class Revision {
    abstract val author: String
    abstract val timestamp: Long
    abstract val id: String

    data class Insertion(
        val text: String,
        override val author: String,
        override val timestamp: Long,
        override val id: String
    ) : Revision()

    data class Deletion(
        val text: String,
        override val author: String,
        override val timestamp: Long,
        override val id: String
    ) : Revision()

    data class FormatChange(
        val oldStyle: SpanStyle,
        val newStyle: SpanStyle,
        override val author: String,
        override val timestamp: Long,
        override val id: String
    ) : Revision()
}

class TrackChangesManager {
    private val _revisions = mutableListOf<Revision>()
    val revisions: List<Revision> get() = _revisions.toList()

    var isTrackingEnabled: Boolean = false

    fun addRevision(revision: Revision) {
        if (isTrackingEnabled) {
            _revisions.add(revision)
        }
    }

    fun acceptRevision(id: String) {
        _revisions.removeAll { it.id == id }
    }

    fun rejectRevision(id: String) {
        _revisions.removeAll { it.id == id }
        // Note: Actual document reversion logic would go here
    }
}
