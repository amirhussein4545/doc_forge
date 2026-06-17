package com.example.domain.model

enum class ViewMode {
    PrintLayout,
    Draft,
    ReadMode,
    FocusMode
}

class ViewModeManager {
    var currentMode: ViewMode = ViewMode.PrintLayout
        private set

    var zoomLevel: Float = 1.0f
        private set
        
    fun switchMode(mode: ViewMode) {
        currentMode = mode
    }
    
    fun setZoom(level: Float) {
        zoomLevel = level.coerceIn(0.1f, 5.0f)
    }
}
