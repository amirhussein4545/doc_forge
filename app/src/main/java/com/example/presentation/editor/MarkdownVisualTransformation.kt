package com.example.presentation.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class MarkdownVisualTransformation(
    private val searchQuery: String = "",
    private val cursors: List<CursorInfo> = emptyList()
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        var outString = ""
        val mapping = mutableListOf<Int>()
        
        // Find intervals to hide
        val hideRanges = mutableListOf<IntRange>()
        val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
        boldRegex.findAll(text.text).forEach { matchResult ->
            hideRanges.add(matchResult.range.first..matchResult.range.first + 1)
            hideRanges.add(matchResult.range.last - 1..matchResult.range.last)
        }
        val italicRegex = Regex("_(.*?)_")
        italicRegex.findAll(text.text).forEach { matchResult ->
            hideRanges.add(matchResult.range.first..matchResult.range.first)
            hideRanges.add(matchResult.range.last..matchResult.range.last)
        }
        
        var currentOffset = 0
        val originalToTransformed = mutableListOf<Int>()
        val transformedToOriginal = mutableListOf<Int>()
        
        for (i in text.text.indices) {
            originalToTransformed.add(currentOffset)
            if (hideRanges.any { i in it }) {
                // skip character
            } else {
                outString += text.text[i]
                transformedToOriginal.add(i)
                currentOffset++
            }
        }
        originalToTransformed.add(currentOffset)
        transformedToOriginal.add(text.text.length)
        
        val annotatedString = buildAnnotatedString {
            append(outString)
            
            boldRegex.findAll(text.text).forEach { matchResult ->
                val start = originalToTransformed[matchResult.range.first]
                val end = originalToTransformed[matchResult.range.last + 1]
                addStyle(
                    style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black),
                    start = start,
                    end = end
                )
            }
            
            italicRegex.findAll(text.text).forEach { matchResult ->
                val start = originalToTransformed[matchResult.range.first]
                val end = originalToTransformed[matchResult.range.last + 1]
                addStyle(
                    style = SpanStyle(fontStyle = FontStyle.Italic, color = Color.Black),
                    start = start,
                    end = end
                )
            }
            
            // Search highlighting
            if (searchQuery.isNotEmpty()) {
                val searchRegex = Regex(Regex.escape(searchQuery), RegexOption.IGNORE_CASE)
                searchRegex.findAll(text.text).forEach { matchResult ->
                    val start = originalToTransformed[matchResult.range.first]
                    val end = originalToTransformed[matchResult.range.last + 1]
                    addStyle(
                        style = SpanStyle(background = Color.Yellow, color = Color.Black),
                        start = start,
                        end = end
                    )
                }
            }
            
            // Draw remote cursors (simple mapped position)
            cursors.forEach { cursor ->
                val origPos = if (cursor.position >= text.text.length) text.text.length - 1 else cursor.position
                if (origPos >= 0) {
                    val pos = originalToTransformed[origPos]
                    if (pos < outString.length) {
                         addStyle(
                            style = SpanStyle(background = Color(android.graphics.Color.parseColor(cursor.colorHex)), color = Color.White),
                            start = pos,
                            end = pos + 1
                        )
                    }
                }
            }
        }
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset >= originalToTransformed.size) return originalToTransformed.lastOrNull() ?: 0
                return originalToTransformed[offset]
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset >= transformedToOriginal.size) return transformedToOriginal.lastOrNull() ?: 0
                return transformedToOriginal[offset]
            }
        }
        
        return TransformedText(annotatedString, offsetMapping)
    }
}
