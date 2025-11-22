package com.katoki.voicenavigation.mapper

import android.graphics.Point

/**
 * GridMapper converts grid-based positions (e.g., "A1", "C5") to screen coordinates.
 * The grid system divides the screen into cells for easier voice-based targeting.
 */
class GridMapper(private val screenWidth: Int, private val screenHeight: Int) {
    
    companion object {
        private const val GRID_COLUMNS = 10 // A-J
        private const val GRID_ROWS = 10    // 1-10
    }
    
    private val cellWidth = screenWidth / GRID_COLUMNS
    private val cellHeight = screenHeight / GRID_ROWS
    
    /**
     * Converts a grid position (e.g., "A5") to screen coordinates.
     * @param gridPosition Grid position in format [A-J][1-10]
     * @return Point with x,y coordinates, or null if invalid
     */
    fun gridToCoordinates(gridPosition: String): Point? {
        if (gridPosition.length < 2) return null
        
        val column = gridPosition[0].uppercaseChar()
        val rowStr = gridPosition.substring(1)
        
        // Validate column (A-J)
        if (column < 'A' || column > 'J') return null
        
        // Validate and parse row (1-10)
        val row = rowStr.toIntOrNull() ?: return null
        if (row < 1 || row > GRID_ROWS) return null
        
        // Calculate center of the grid cell
        val columnIndex = column - 'A'
        val rowIndex = row - 1
        
        val x = columnIndex * cellWidth + cellWidth / 2
        val y = rowIndex * cellHeight + cellHeight / 2
        
        return Point(x, y)
    }
    
    /**
     * Validates if a grid position is in the correct format.
     * @param gridPosition Grid position to validate
     * @return true if valid, false otherwise
     */
    fun isValidGridPosition(gridPosition: String): Boolean {
        return gridToCoordinates(gridPosition) != null
    }
}
