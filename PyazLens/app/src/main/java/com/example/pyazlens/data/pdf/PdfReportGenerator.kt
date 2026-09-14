package com.example.pyazlens.data.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.pyazlens.data.language.AppStrings
import com.example.pyazlens.data.network.AnalyzeResponse
import com.example.pyazlens.data.network.OnionResult
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    fun generateAndShareReport(
        context: Context,
        result: AnalyzeResponse,
        currentLanguage: String
    ) {
        try {
            val strings = AppStrings.getStrings(currentLanguage)

            val document = PdfDocument()

            val pageWidth = 595 // A4 width in points (8.27 inches * 72)
            val pageHeight = 842 // A4 height in points (11.69 inches * 72)
            val margin = 36f
            val printableWidth = pageWidth - (margin * 2)

            var pageNumber = 1

            var page = document.startPage(
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            )

            var canvas = page.canvas
            var y = 40f

            // Colors
            val colorPurple = 0xFF511D50.toInt()
            val colorPurpleLight = 0xFFF5EFF6.toInt()
            val colorGreen = 0xFF73C943.toInt()
            val colorGradeA = 0xFF55A83A.toInt()
            val colorURS = 0xFFD49320.toInt()
            val colorReject = 0xFFD94A4A.toInt()
            val colorGrayDark = 0xFF4A4A4A.toInt()
            val colorGrayLight = 0xFFF8F7F9.toInt()
            val colorBorder = 0xFFE5E0E6.toInt()

            // Paints
            val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 24f
                isFakeBoldText = true
                color = colorPurple
            }

            val subtitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 12f
                color = colorGrayDark
            }

            val sectionTitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 15f
                isFakeBoldText = true
                color = colorPurple
            }

            val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 11f
                color = android.graphics.Color.BLACK
            }

            val bodyBoldPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 11f
                isFakeBoldText = true
                color = android.graphics.Color.BLACK
            }

            val smallPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 9.5f
                color = colorGrayDark
            }

            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
            }

            val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 1f
                color = colorBorder
            }

            fun newPageIfNeeded(requiredHeight: Float) {
                if (y + requiredHeight > pageHeight - 50f) {
                    // Draw Footer on finishing page
                    val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                        textSize = 9f
                        color = android.graphics.Color.GRAY
                    }
                    canvas.drawText(
                        "PyazLens • ${strings.pdfSubTitle} • Page $pageNumber",
                        margin,
                        pageHeight - 25f,
                        footerPaint
                    )

                    document.finishPage(page)
                    pageNumber++

                    page = document.startPage(
                        PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    )
                    canvas = page.canvas
                    y = 40f
                }
            }

            // =========================================================
            // BRAND HEADER BANNER
            // =========================================================
            bgPaint.color = colorPurple
            canvas.drawRoundRect(RectF(margin, y, pageWidth - margin, y + 65f), 12f, 12f, bgPaint)

            // Header Title Inside Banner
            val headerTitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 22f
                isFakeBoldText = true
                color = android.graphics.Color.WHITE
            }
            canvas.drawText("PyazLens", margin + 16f, y + 34f, headerTitlePaint)

            val headerSubPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 11f
                color = colorGreen
                isFakeBoldText = true
            }
            canvas.drawText("•  AI Onion Quality Inspection Report", margin + 118f, y + 34f, headerSubPaint)

            val dateStr = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault()).format(Date())
            val headerDatePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 9.5f
                color = android.graphics.Color.LTGRAY
            }
            canvas.drawText(dateStr, margin + 16f, y + 52f, headerDatePaint)

            y += 80f

            // =========================================================
            // OVERVIEW SUMMARY CARDS
            // =========================================================
            newPageIfNeeded(100f)

            canvas.drawText(strings.overallSummaryTitle, margin, y, sectionTitlePaint)
            y += 14f

            val totalOnions = result.total_onions
            val gradeA = result.summary.grade_a
            val gradeAPercent = result.summary.grade_a_percentage.toInt()
            val urs = result.summary.grade_urs
            val ursPercent = result.summary.grade_urs_percentage.toInt()
            val reject = result.summary.rejected
            val rejectPercent = result.summary.rejected_percentage.toInt()

            val cardWidth = (printableWidth - 24f) / 4f
            val cardHeight = 54f

            val metrics = listOf(
                Quad("Total Onions", "$totalOnions", "100%", colorPurple),
                Quad(AppStrings.translateGrade("Grade A", strings), "$gradeA", "$gradeAPercent%", colorGradeA),
                Quad(AppStrings.translateGrade("URS", strings), "$urs", "$ursPercent%", colorURS),
                Quad(AppStrings.translateGrade("REJECT", strings), "$reject", "$rejectPercent%", colorReject)
            )

            metrics.forEachIndexed { idx, item ->
                val x = margin + (idx * (cardWidth + 8f))
                val rect = RectF(x, y, x + cardWidth, y + cardHeight)

                bgPaint.color = colorPurpleLight
                canvas.drawRoundRect(rect, 8f, 8f, bgPaint)
                canvas.drawRoundRect(rect, 8f, 8f, strokePaint)

                val cardValPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 16f
                    isFakeBoldText = true
                    color = item.color
                }
                canvas.drawText(item.valStr, x + 10f, y + 24f, cardValPaint)

                val cardTitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 9f
                    color = colorGrayDark
                }
                canvas.drawText(item.label, x + 10f, y + 38f, cardTitlePaint)

                val cardPctPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 9f
                    isFakeBoldText = true
                    color = item.color
                }
                canvas.drawText(item.subStr, x + cardWidth - 32f, y + 24f, cardPctPaint)
            }

            y += cardHeight + 20f

            // =========================================================
            // DEFECT SUMMARY TABLE
            // =========================================================
            newPageIfNeeded(120f)
            canvas.drawText(strings.defectSummaryTitle, margin, y, sectionTitlePaint)
            y += 14f

            val defects = listOf(
                Pair(strings.defectRotten, result.defect_summary.Rotten),
                Pair(strings.defectCutCrack, result.defect_summary.cutCrack),
                Pair(strings.defectSprouted, result.defect_summary.Sprouted),
                Pair(strings.defectSkinDamage, result.defect_summary.skinDamage),
                Pair(strings.defectSunburned, result.defect_summary.Sunburned),
                Pair(strings.defectMisshapen, result.defect_summary.Misshapen)
            )

            val tableRect = RectF(margin, y, pageWidth - margin, y + 80f)
            bgPaint.color = android.graphics.Color.WHITE
            canvas.drawRoundRect(tableRect, 8f, 8f, bgPaint)
            canvas.drawRoundRect(tableRect, 8f, 8f, strokePaint)

            var gridY = y + 20f
            var gridX = margin + 16f
            val colW = printableWidth / 3f

            defects.forEachIndexed { idx, pair ->
                val cX = gridX + ((idx % 3) * colW)
                val cY = gridY + ((idx / 3) * 26f)

                canvas.drawText("${pair.first}: ", cX, cY, bodyPaint)
                val countPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 11f
                    isFakeBoldText = true
                    color = if (pair.second > 0) colorReject else colorPurple
                }
                canvas.drawText("${pair.second}", cX + 85f, cY, countPaint)
            }

            y += 96f

            // =========================================================
            // INDIVIDUAL ONION RESULTS
            // =========================================================
            newPageIfNeeded(40f)
            canvas.drawText(strings.individualResultsTitle, margin, y, sectionTitlePaint)
            y += 18f

            result.onions.forEachIndexed { index, onion ->
                newPageIfNeeded(95f)

                val onionBoxRect = RectF(margin, y, pageWidth - margin, y + 86f)
                bgPaint.color = colorGrayLight
                canvas.drawRoundRect(onionBoxRect, 8f, 8f, bgPaint)
                canvas.drawRoundRect(onionBoxRect, 8f, 8f, strokePaint)

                val onionNumStr = "Onion #${(index + 1).toString().padStart(2, '0')}"
                canvas.drawText(onionNumStr, margin + 12f, y + 22f, bodyBoldPaint)

                // Grade Pill
                val gradeDisplay = AppStrings.translateGrade(onion.grade, strings)
                val gradePillColor = when {
                    onion.grade.equals("REJECT", ignoreCase = true) -> colorReject
                    onion.grade.equals("Grade A", ignoreCase = true) || onion.grade.equals("A", ignoreCase = true) -> colorGradeA
                    else -> colorURS
                }
                val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = gradePillColor
                    style = Paint.Style.FILL
                }
                val pillRect = RectF(pageWidth - margin - 85f, y + 8f, pageWidth - margin - 10f, y + 26f)
                canvas.drawRoundRect(pillRect, 6f, 6f, pillPaint)

                val pillTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 9.5f
                    isFakeBoldText = true
                    color = android.graphics.Color.WHITE
                }
                canvas.drawText(gradeDisplay, pillRect.left + 8f, pillRect.top + 13f, pillTextPaint)

                // Dimensions
                val dimStr = "Dia: ${onion.size.diameter_mm}mm  |  W: ${onion.size.width_mm}mm  |  H: ${onion.size.height_mm}mm"
                canvas.drawText(dimStr, margin + 12f, y + 38f, smallPaint)

                // Defects
                val defectStr = if (onion.defects.isEmpty()) {
                    "${strings.classificationLabel}: ${strings.defectNoDefect}"
                } else {
                    val defectsText = onion.defects.joinToString(", ") { d ->
                        "${AppStrings.translateDefect(d.name, strings)} (${(d.confidence * 100).toInt()}%)"
                    }
                    "${strings.detectedDefectsLabel}: $defectsText"
                }
                canvas.drawText(defectStr, margin + 12f, y + 54f, smallPaint)

                // Reason
                val reasonText = if (onion.grade_reason.isNotBlank()) "Reason: ${onion.grade_reason}" else ""
                if (reasonText.isNotBlank()) {
                    val reasonPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                        textSize = 8.5f
                        color = android.graphics.Color.DKGRAY
                    }
                    canvas.drawText(reasonText, margin + 12f, y + 70f, reasonPaint)
                }

                y += 94f
            }

            // Finish Last Page Footer
            val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 9f
                color = android.graphics.Color.GRAY
            }
            canvas.drawText(
                "PyazLens • ${strings.pdfSubTitle} • Page $pageNumber",
                margin,
                pageHeight - 25f,
                footerPaint
            )

            document.finishPage(page)

            // Save PDF
            val fileName = "PyazLens_Report_${System.currentTimeMillis()}.pdf"
            val file = File(context.cacheDir, fileName)

            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
            document.close()

            // Open Share Intent
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(
                Intent.createChooser(shareIntent, "PyazLens Report PDF")
            )

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Unable to generate PDF report: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private data class Quad<A, B, C, D>(
        val label: A,
        val valStr: B,
        val subStr: C,
        val color: D
    )
}
