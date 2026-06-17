package com.example.domain.model

import androidx.compose.ui.geometry.Rect
import kotlinx.serialization.Serializable

@Serializable
data class ImageElement(
    val id: String,
    val sourcePath: String, // URI or path
    val width: Float,
    val height: Float,
    val x: Float,
    val y: Float,
    val rotation: Float,
    val opacity: Float = 1f,
    val brightness: Float = 0f,
    val contrast: Float = 0f,
    val altTextTitle: String = "",
    val altTextDescription: String = "",
    val isDecorative: Boolean = false
)
