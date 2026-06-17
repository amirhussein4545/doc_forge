package com.example.domain.model

// Simplified language checking stub
class SpellCheckManager {
    
    fun checkSpelling(text: String): List<SpellingError> {
        // Stub implementation
        val errors = mutableListOf<SpellingError>()
        if (text.contains("teh")) {
            val idx = text.indexOf("teh")
            errors.add(SpellingError("teh", idx, idx + 3, listOf("the", "ten")))
        }
        return errors
    }
    
    fun checkGrammar(text: String): List<GrammarError> {
        // Stub for LanguageTool API
        return emptyList()
    }
}

data class SpellingError(
    val word: String,
    val startIndex: Int,
    val endIndex: Int,
    val suggestions: List<String>
)

data class GrammarError(
    val message: String,
    val startIndex: Int,
    val endIndex: Int
)
