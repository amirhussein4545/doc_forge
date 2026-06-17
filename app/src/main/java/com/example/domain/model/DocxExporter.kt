package com.example.domain.model

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File

class DocxExporter {

    fun exportToDocx(context: Context, content: String, fileName: String): Uri? {
        val document = XWPFDocument()

        content.split("\n").forEach { line ->
            val paragraph = document.createParagraph()
            val run = paragraph.createRun()

            // Basic parsing for export formatting
            if (line.startsWith("- ")) {
                run.setText("• " + line.removePrefix("- "))
            } else {
                run.setText(line.replace("**", "").replace("_", ""))
            }
        }

        return saveDocxToDownloads(context, document, fileName)
    }

    private fun saveDocxToDownloads(context: Context, document: XWPFDocument, fileName: String): Uri? {
        var uri: Uri? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.docx")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        document.write(outputStream)
                    }
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, "$fileName.docx")
                file.outputStream().use { outputStream ->
                    document.write(outputStream)
                }
                uri = Uri.fromFile(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            document.close()
        }
        return uri
    }
}
