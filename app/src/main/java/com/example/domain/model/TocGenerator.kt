package com.example.domain.model

data class TocEntry(
    val title: String,
    val pageNumber: Int,
    val level: Int,
    val id: String
)

class TocGenerator {
    fun generateToc(documentText: String, stylesMap: Map<Int, String>): List<TocEntry> {
        // Simple mock TOC generator
        // In reality, this would scan document nodes for Heading 1-6 styles
        val entries = mutableListOf<TocEntry>()
        var page = 1
        
        documentText.lines().forEachIndexed { index, line ->
            if (line.startsWith("# ")) {
                entries.add(TocEntry(line.removePrefix("# "), page, 1, "id_$index"))
            } else if (line.startsWith("## ")) {
                entries.add(TocEntry(line.removePrefix("## "), page, 2, "id_$index"))
            }
            if (index % 50 == 0) page++
        }
        return entries
    }
}
