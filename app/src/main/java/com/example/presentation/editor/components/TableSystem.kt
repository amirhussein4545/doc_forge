package com.example.presentation.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class TableCell(
    val text: String,
    val rowSpan: Int = 1,
    val colSpan: Int = 1,
    val backgroundColor: Color? = null
)

data class TableModel(
    val rows: Int,
    val cols: Int,
    val cells: List<List<TableCell>>,
    val hasHeaderRow: Boolean = false,
    val hasBandedRows: Boolean = false
)

@Composable
fun TableRenderer(
    table: TableModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.border(1.dp, Color.Gray)) {
        table.cells.forEachIndexed { rowIndex, row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEachIndexed { colIndex, cell ->
                    val isHeader = table.hasHeaderRow && rowIndex == 0
                    val isBanded = table.hasBandedRows && rowIndex % 2 == 1
                    
                    val bgColor = cell.backgroundColor 
                        ?: if (isHeader) Color.LightGray 
                        else if (isBanded) Color(0xFFF0F0F0) 
                        else Color.White

                    Box(
                        modifier = Modifier
                            .weight(cell.colSpan.toFloat())
                            .background(bgColor)
                            .border(0.5.dp, Color.LightGray)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = cell.text)
                    }
                }
            }
        }
    }
}
