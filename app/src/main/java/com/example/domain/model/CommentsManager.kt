package com.example.domain.model

data class Comment(
    val id: String,
    val author: String,
    val text: String,
    val timestamp: Long,
    val isResolved: Boolean = false,
    val replies: List<Comment> = emptyList()
)

class CommentsManager {
    private val _comments = mutableListOf<Comment>()
    val comments: List<Comment> get() = _comments.toList()

    fun addComment(comment: Comment) {
        _comments.add(comment)
    }

    fun resolveComment(id: String) {
        val index = _comments.indexOfFirst { it.id == id }
        if (index != -1) {
            _comments[index] = _comments[index].copy(isResolved = true)
        }
    }

    fun deleteComment(id: String) {
        _comments.removeAll { it.id == id }
    }
}
