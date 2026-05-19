package com.shiji.app.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.shiji.app.data.entity.Record
import com.shiji.app.domain.model.BristolType
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfExporter(private val context: Context) {

    fun export(records: List<Record>, title: String = "屎记 · 史册"): Uri {
        val document = PdfDocument()
        val sorted = records.sortedBy { it.timestamp }

        // Page 1: Title page
        val titlePage = document.startPage(
            PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        )
        drawTitlePage(titlePage.canvas, title, sorted.size)
        document.finishPage(titlePage)

        // Content pages
        val itemsPerPage = 2
        val totalPages = (sorted.size + itemsPerPage - 1) / itemsPerPage

        for (pageIndex in 0 until totalPages) {
            val page = document.startPage(
                PdfDocument.PageInfo.Builder(595, 842, pageIndex + 2).create()
            )
            drawContentPage(page.canvas, sorted, pageIndex, itemsPerPage, pageIndex + 1)
            document.finishPage(page)
        }

        // Save to cache
        val pdfDir = File(context.cacheDir, "pdfs")
        pdfDir.mkdirs()
        val pdfFile = File(pdfDir, "shiji_export_${System.currentTimeMillis()}.pdf")
        document.writeTo(pdfFile.outputStream())
        document.close()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
    }

    private fun drawTitlePage(canvas: Canvas, title: String, recordCount: Int) {
        val width = canvas.width.toFloat()
        val height = canvas.height.toFloat()

        // Background
        canvas.drawColor(0xFFFAF4E8.toInt())

        // Border
        val borderPaint = Paint().apply {
            color = 0xFFC4A265.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawRect(40f, 40f, width - 40f, height - 40f, borderPaint)

        // Inner border
        canvas.drawRect(50f, 50f, width - 50f, height - 50f, borderPaint)

        // Title
        val titlePaint = Paint().apply {
            color = 0xFF2C2416.toInt()
            textSize = 48f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isFakeBoldText = true
        }
        canvas.drawText(title, width / 2, height * 0.35f, titlePaint)

        // Subtitle
        val subPaint = Paint().apply {
            color = 0xFF6B5D4F.toInt()
            textSize = 20f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("—— 本纪 · 实录 ——", width / 2, height * 0.42f, subPaint)

        // Record count
        val countPaint = Paint().apply {
            color = 0xFFC04040.toInt()
            textSize = 18f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("共收录 $recordCount 条史录", width / 2, height * 0.50f, countPaint)

        // Date
        val datePaint = Paint().apply {
            color = 0xFF6B5D4F.toInt()
            textSize = 14f
            textAlign = Paint.Align.CENTER
        }
        val dateStr = SimpleDateFormat("yyyy年M月d日", Locale.CHINESE).format(Date())
        canvas.drawText("撰于 $dateStr", width / 2, height * 0.56f, datePaint)

        // Footer
        val footerPaint = Paint().apply {
            color = 0xFFC4A265.toInt()
            textSize = 14f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("太史公 著", width / 2, height * 0.90f, footerPaint)
    }

    private fun drawContentPage(
        canvas: Canvas,
        records: List<Record>,
        pageIndex: Int,
        itemsPerPage: Int,
        pageNumber: Int
    ) {
        val width = canvas.width.toFloat()
        val height = canvas.height.toFloat()

        canvas.drawColor(0xFFFAF4E8.toInt())

        val startIndex = pageIndex * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, records.size)

        // Page header
        val headerPaint = Paint().apply {
            color = 0xFF2C2416.toInt()
            textSize = 16f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("屎记 · 史册", width / 2, 60f, headerPaint)

        // Divider
        val dividerPaint = Paint().apply {
            color = 0xFFC4A265.toInt()
            strokeWidth = 1f
        }
        canvas.drawLine(40f, 75f, width - 40f, 75f, dividerPaint)

        val listPaint = Paint().apply {
            color = 0xFF2C2416.toInt()
            textSize = 13f
        }
        val smallPaint = Paint().apply {
            color = 0xFF6B5D4F.toInt()
            textSize = 11f
        }
        val numberPaint = Paint().apply {
            color = 0xFFC04040.toInt()
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        var y = 120f

        for (i in startIndex until endIndex) {
            val record = records[i]
            val dateFormat = SimpleDateFormat("yyyy年M月d日 HH:mm", Locale.CHINESE)

            // Entry number
            canvas.drawText("卷${i + 1}", 50f, y, numberPaint)

            // Date and bristol type
            val bristol = BristolType.fromValue(record.bristolType)
            canvas.drawText(
                "${dateFormat.format(Date(record.timestamp))}    ${bristol.emoji} ${bristol.label}",
                50f, y + 22f, listPaint
            )

            // Details line
            canvas.drawText(
                "色：${record.color}  |  气味：${"★".repeat(record.odorLevel)}  |  顺畅：${"★".repeat(record.smoothness)}",
                50f, y + 42f, smallPaint
            )

            // Commentary
            val commentaryPaint = Paint().apply {
                color = 0xFF4A3728.toInt()
                textSize = 13f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            }
            canvas.drawText(record.commentary, 50f, y + 62f, commentaryPaint)

            // Note
            if (!record.note.isNullOrBlank()) {
                canvas.drawText("「${record.note}」", 50f, y + 80f, smallPaint)
            }

            // Divider between entries
            y += 240f
            if (i < endIndex - 1) {
                canvas.drawLine(40f, y - 30f, width - 40f, y - 30f, dividerPaint)
            }
        }

        // Page number
        val pagePaint = Paint().apply {
            color = 0xFF6B5D4F.toInt()
            textSize = 12f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("— $pageNumber —", width / 2, height - 40f, pagePaint)
    }
}
