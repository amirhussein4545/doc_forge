package com.example.domain.model

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.OutputStream

class PdfExporter {

    fun exportToPdf(context: Context, content: String, fileName: String): Uri? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 Size
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val defaultPaint = Paint().apply {
            textSize = 14f
            color = Color.BLACK
            isAntiAlias = true
        }

        val boldPaint = Paint(defaultPaint).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val italicPaint = Paint(defaultPaint).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        }

        val margin = 50f
        var currentY = margin
        val lineHeight = defaultPaint.descent() - defaultPaint.ascent()
        val pageHeight = pageInfo.pageHeight

        content.split("\n").forEach { line ->
            if (currentY + lineHeight > pageHeight - margin) {
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                currentY = margin
            }

            var textLine = line
            var currentX = margin

            // Very simple inline styling parsing for PDF drawing
            // Note: A robust implementation would use a TextLayout or spannables
            if (textLine.startsWith("- ")) {
                canvas.drawText("•", currentX, currentY, defaultPaint)
                currentX += 15f
                textLine = textLine.removePrefix("- ")
                canvas.drawText(textLine, currentX, currentY, defaultPaint)
            } else {
                // To keep it simple, we draw the whole line if no formatting,
                // stripping markdown characters for basic display
                var displayText = textLine.replace("**", "").replace("_", "")
                canvas.drawText(displayText, currentX, currentY, defaultPaint)
            }

            currentY += lineHeight
            // Add a little extra space for paragraphs
            if (line.isEmpty()) {
                currentY += lineHeight / 2
            }
        }

        pdfDocument.finishPage(page)

        return savePdfToDownloads(context, pdfDocument, fileName)
    }

    private fun savePdfToDownloads(context: Context, pdfDocument: PdfDocument, fileName: String): Uri? {
        var uri: Uri? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, "$fileName.pdf")
                file.outputStream().use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                uri = Uri.fromFile(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
        return uri
    }
}
