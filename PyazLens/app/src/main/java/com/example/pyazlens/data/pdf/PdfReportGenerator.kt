package com.example.pyazlens.data.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.TextPaint
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.pyazlens.data.language.AppStrings
import com.example.pyazlens.data.network.AnalyzeResponse
import com.example.pyazlens.data.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

object PdfReportGenerator {

    fun generateAndShareReport(
        context: Context,
        result: AnalyzeResponse,
        currentLanguage: String,
        imageUri: Uri? = null,
        imageUrl: String? = null
    ) {
        Toast.makeText(context, "Generating PDF report...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val strings = AppStrings.getStrings(currentLanguage)

                val document = PdfDocument()

                val pageWidth = 595
                val pageHeight = 842
                val margin = 36f
                val printableWidth = pageWidth - margin * 2

                var pageNumber = 1

                var page = document.startPage(
                    PdfDocument.PageInfo.Builder(
                        pageWidth,
                        pageHeight,
                        pageNumber
                    ).create()
                )

                var canvas = page.canvas
                var y = 40f

                // =========================================================
                // COLORS
                // =========================================================

                val colorPurple = 0xFF511D50.toInt()
                val colorPurpleLight = 0xFFF5EFF6.toInt()
                val colorGreen = 0xFF73C943.toInt()
                val colorGradeA = 0xFF55A83A.toInt()
                val colorURS = 0xFFD49320.toInt()
                val colorReject = 0xFFD94A4A.toInt()
                val colorGrayDark = 0xFF4A4A4A.toInt()
                val colorGrayLight = 0xFFF8F7F9.toInt()
                val colorBorder = 0xFFE5E0E6.toInt()

                // =========================================================
                // PAINTS
                // =========================================================

                val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 24f
                    isFakeBoldText = true
                    color = colorPurple
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

                // =========================================================
                // FOOTER
                // =========================================================

                fun drawFooter() {
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
                }

                // =========================================================
                // NEW PAGE
                // =========================================================

                fun startNewPage() {
                    drawFooter()
                    document.finishPage(page)

                    pageNumber++

                    page = document.startPage(
                        PdfDocument.PageInfo.Builder(
                            pageWidth,
                            pageHeight,
                            pageNumber
                        ).create()
                    )

                    canvas = page.canvas
                    y = 40f
                }

                fun newPageIfNeeded(requiredHeight: Float) {
                    if (y + requiredHeight > pageHeight - 55f) {
                        startNewPage()
                    }
                }

                // =========================================================
                // LOAD IMAGE FROM URL OR URI
                // =========================================================

                fun loadBitmap(source: Any?): Bitmap? {
                    if (source == null) {
                        Log.d("PdfReportGenerator", "loadBitmap: Source parameter is NULL")
                        return null
                    }

                    if (source is Uri) {
                        Log.d("PdfReportGenerator", "loadBitmap: Loading directly from Uri = '$source'")
                        return try {
                            val bitmap = context.contentResolver.openInputStream(source)?.use { stream ->
                                BitmapFactory.decodeStream(stream)
                            }
                            if (bitmap != null) {
                                Log.d("PdfReportGenerator", "loadBitmap: Successfully loaded bitmap from Uri (${bitmap.width}x${bitmap.height})")
                            } else {
                                Log.e("PdfReportGenerator", "loadBitmap: BitmapFactory.decodeStream returned null for Uri '$source'")
                            }
                            bitmap
                        } catch (e: Exception) {
                            Log.e("PdfReportGenerator", "loadBitmap: Exception loading Uri '$source'", e)
                            null
                        }
                    }

                    val urlString = source.toString().trim()
                    if (urlString.isBlank()) {
                        Log.d("PdfReportGenerator", "loadBitmap: urlString is blank")
                        return null
                    }

                    Log.d("PdfReportGenerator", "loadBitmap: Processing String source = '$urlString'")

                    if (urlString.startsWith("content://") || urlString.startsWith("file://")) {
                        Log.d("PdfReportGenerator", "loadBitmap: Parsing local URI string '$urlString'")
                        return try {
                            val parsedUri = Uri.parse(urlString)
                            val bitmap = context.contentResolver.openInputStream(parsedUri)?.use { stream ->
                                BitmapFactory.decodeStream(stream)
                            }
                            if (bitmap != null) {
                                Log.d("PdfReportGenerator", "loadBitmap: Successfully loaded bitmap from local URI string (${bitmap.width}x${bitmap.height})")
                            } else {
                                Log.e("PdfReportGenerator", "loadBitmap: BitmapFactory.decodeStream returned null for local URI string '$urlString'")
                            }
                            bitmap
                        } catch (e: Exception) {
                            Log.e("PdfReportGenerator", "loadBitmap: Exception loading local URI string '$urlString'", e)
                            null
                        }
                    }

                    // Build candidate URLs to attempt
                    val candidateUrls = mutableListOf<String>()

                    if (urlString.startsWith("http://") || urlString.startsWith("https://")) {
                        candidateUrls.add(urlString)
                    } else if (urlString.startsWith("/") || urlString.startsWith("inspection-crops")) {
                        candidateUrls.add(RetrofitClient.imageUrl(urlString))
                    } else {
                        // Raw filename or UUID object key (e.g. "647d0e34-....jpg" or "inspection-images/647d0e34-....jpg")
                        val cleanFilename = urlString.substringAfterLast("/")
                        candidateUrls.add("https://jfhwmjbcwhiquecdsazs.supabase.co/storage/v1/object/public/inspection-images/$cleanFilename")
                        candidateUrls.add(RetrofitClient.imageUrl(urlString))
                    }

                    for (targetUrl in candidateUrls) {
                        Log.d("PdfReportGenerator", "loadBitmap: Attempting HTTP download from targetUrl = '$targetUrl'")
                        try {
                            val url = URL(targetUrl)
                            val connection = url.openConnection() as HttpURLConnection
                            connection.connectTimeout = 15000
                            connection.readTimeout = 20000
                            connection.instanceFollowRedirects = true
                            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile; PyazLens)")
                            connection.doInput = true
                            connection.connect()

                            val responseCode = connection.responseCode
                            Log.d("PdfReportGenerator", "loadBitmap: HTTP response code = $responseCode for '$targetUrl'")

                            if (responseCode in 200..299) {
                                val bytes = connection.inputStream.use { it.readBytes() }
                                connection.disconnect()
                                Log.d("PdfReportGenerator", "loadBitmap: Downloaded byte count = ${bytes.size} bytes for '$targetUrl'")

                                if (bytes.isNotEmpty()) {
                                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                    if (bitmap == null) {
                                        Log.e("PdfReportGenerator", "loadBitmap: BitmapFactory.decodeByteArray returned NULL for '$targetUrl'")
                                    } else {
                                        Log.d("PdfReportGenerator", "loadBitmap: Successfully loaded HTTP bitmap (${bitmap.width}x${bitmap.height}) for '$targetUrl'")
                                        return bitmap
                                    }
                                } else {
                                    Log.e("PdfReportGenerator", "loadBitmap: Empty byte array (0 bytes) for '$targetUrl'")
                                }
                            } else {
                                Log.e("PdfReportGenerator", "loadBitmap: HTTP Error $responseCode ${connection.responseMessage} for '$targetUrl'")
                                connection.disconnect()
                            }
                        } catch (e: Exception) {
                            Log.e("PdfReportGenerator", "loadBitmap: Exception downloading '$targetUrl'", e)
                        }
                    }

                    Log.e("PdfReportGenerator", "loadBitmap: All candidate URLs failed for '$urlString'")
                    return null
                }

                // =========================================================
                // DRAW IMAGE FITTED INSIDE RECT
                // =========================================================

                fun drawBitmap(
                    bitmap: Bitmap,
                    destination: RectF
                ) {
                    val bitmapWidth = bitmap.width.toFloat()
                    val bitmapHeight = bitmap.height.toFloat()

                    val scale = min(
                        destination.width() / bitmapWidth,
                        destination.height() / bitmapHeight
                    )

                    val drawWidth = bitmapWidth * scale
                    val drawHeight = bitmapHeight * scale

                    val left =
                        destination.left +
                                (destination.width() - drawWidth) / 2f

                    val top =
                        destination.top +
                                (destination.height() - drawHeight) / 2f

                    val destRect = RectF(
                        left,
                        top,
                        left + drawWidth,
                        top + drawHeight
                    )

                    canvas.drawBitmap(
                        bitmap,
                        null,
                        destRect,
                        Paint(Paint.ANTI_ALIAS_FLAG)
                    )
                }

                // =========================================================
                // PAGE 1 — HEADER
                // =========================================================

                bgPaint.color = colorPurple

                canvas.drawRoundRect(
                    RectF(
                        margin,
                        y,
                        pageWidth - margin,
                        y + 65f
                    ),
                    12f,
                    12f,
                    bgPaint
                )

                val headerTitlePaint =
                    TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                        textSize = 22f
                        isFakeBoldText = true
                        color = android.graphics.Color.WHITE
                    }

                canvas.drawText(
                    "PyazLens",
                    margin + 16f,
                    y + 34f,
                    headerTitlePaint
                )

                val headerSubPaint =
                    TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                        textSize = 11f
                        color = colorGreen
                        isFakeBoldText = true
                    }

                canvas.drawText(
                    "• AI Onion Quality Inspection Report",
                    margin + 118f,
                    y + 34f,
                    headerSubPaint
                )

                val dateStr =
                    SimpleDateFormat(
                        "dd MMM yyyy • HH:mm",
                        Locale.getDefault()
                    ).format(Date())

                val headerDatePaint =
                    TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                        textSize = 9.5f
                        color = android.graphics.Color.LTGRAY
                    }

                canvas.drawText(
                    dateStr,
                    margin + 16f,
                    y + 52f,
                    headerDatePaint
                )

                y += 80f

                // =========================================================
                // ORIGINAL BATCH IMAGE
                // =========================================================

                newPageIfNeeded(250f)

                canvas.drawText(
                    "Original Batch Image",
                    margin,
                    y,
                    sectionTitlePaint
                )

                y += 12f

                val imageRect = RectF(
                    margin,
                    y,
                    pageWidth - margin,
                    y + 230f
                )

                bgPaint.color = colorGrayLight

                canvas.drawRoundRect(
                    imageRect,
                    10f,
                    10f,
                    bgPaint
                )

                canvas.drawRoundRect(
                    imageRect,
                    10f,
                    10f,
                    strokePaint
                )

                Log.d("PdfReportGenerator", "=== DIAGNOSING ORIGINAL BATCH IMAGE ===")
                Log.d("PdfReportGenerator", "result.image_url = '${result.image_url}'")
                Log.d("PdfReportGenerator", "fallback imageUrl = '$imageUrl'")
                Log.d("PdfReportGenerator", "fallback imageUri = '$imageUri'")

                var originalBitmap: Bitmap? = loadBitmap(result.image_url)

                if (originalBitmap == null && !imageUrl.isNullOrBlank()) {
                    Log.d("PdfReportGenerator", "result.image_url load failed, attempting fallback imageUrl: '$imageUrl'")
                    originalBitmap = loadBitmap(imageUrl)
                }

                if (originalBitmap == null && imageUri != null) {
                    Log.d("PdfReportGenerator", "imageUrl load failed, attempting fallback imageUri: '$imageUri'")
                    originalBitmap = loadBitmap(imageUri)
                }

                if (originalBitmap != null) {
                    Log.d("PdfReportGenerator", "Original batch image successfully resolved (${originalBitmap.width}x${originalBitmap.height})")
                    drawBitmap(
                        originalBitmap,
                        RectF(
                            imageRect.left + 6f,
                            imageRect.top + 6f,
                            imageRect.right - 6f,
                            imageRect.bottom - 6f
                        )
                    )
                } else {
                    Log.e("PdfReportGenerator", "ALL attempts to load original batch image FAILED.")
                    val unavailablePaint =
                        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                            textSize = 11f
                            color = colorGrayDark
                        }

                    canvas.drawText(
                        "Original image unavailable",
                        margin + 16f,
                        y + 115f,
                        unavailablePaint
                    )
                }

                y += 250f

                // =========================================================
                // SUMMARY
                // =========================================================

                newPageIfNeeded(100f)

                canvas.drawText(
                    strings.overallSummaryTitle,
                    margin,
                    y,
                    sectionTitlePaint
                )

                y += 14f

                val totalOnions = result.total_onions
                val gradeA = result.summary.grade_a
                val gradeAPercent =
                    result.summary.grade_a_percentage.toInt()

                val urs = result.summary.grade_urs
                val ursPercent =
                    result.summary.grade_urs_percentage.toInt()

                val reject = result.summary.rejected
                val rejectPercent =
                    result.summary.rejected_percentage.toInt()

                val cardWidth =
                    (printableWidth - 24f) / 4f

                val cardHeight = 54f

                val metrics = listOf(
                    Triple(
                        "Total Onions",
                        "$totalOnions",
                        "100%"
                    ),
                    Triple(
                        AppStrings.translateGrade(
                            "Grade A",
                            strings
                        ),
                        "$gradeA",
                        "$gradeAPercent%"
                    ),
                    Triple(
                        AppStrings.translateGrade(
                            "URS",
                            strings
                        ),
                        "$urs",
                        "$ursPercent%"
                    ),
                    Triple(
                        AppStrings.translateGrade(
                            "REJECT",
                            strings
                        ),
                        "$reject",
                        "$rejectPercent%"
                    )
                )

                val metricColors = listOf(
                    colorPurple,
                    colorGradeA,
                    colorURS,
                    colorReject
                )

                metrics.forEachIndexed { index, metric ->

                    val x =
                        margin +
                                index * (cardWidth + 8f)

                    val rect = RectF(
                        x,
                        y,
                        x + cardWidth,
                        y + cardHeight
                    )

                    bgPaint.color = colorPurpleLight

                    canvas.drawRoundRect(
                        rect,
                        8f,
                        8f,
                        bgPaint
                    )

                    canvas.drawRoundRect(
                        rect,
                        8f,
                        8f,
                        strokePaint
                    )

                    val valuePaint =
                        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                            textSize = 16f
                            isFakeBoldText = true
                            color = metricColors[index]
                        }

                    canvas.drawText(
                        metric.second,
                        x + 10f,
                        y + 24f,
                        valuePaint
                    )

                    val labelPaint =
                        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                            textSize = 9f
                            color = colorGrayDark
                        }

                    canvas.drawText(
                        metric.first,
                        x + 10f,
                        y + 38f,
                        labelPaint
                    )

                    val percentPaint =
                        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                            textSize = 9f
                            isFakeBoldText = true
                            color = metricColors[index]
                        }

                    canvas.drawText(
                        metric.third,
                        x + cardWidth - 32f,
                        y + 24f,
                        percentPaint
                    )
                }

                y += cardHeight + 20f

                // =========================================================
                // DEFECT SUMMARY
                // =========================================================

                newPageIfNeeded(120f)

                canvas.drawText(
                    strings.defectSummaryTitle,
                    margin,
                    y,
                    sectionTitlePaint
                )

                y += 14f

                val defects = listOf(
                    Pair(
                        strings.defectRotten,
                        result.defect_summary.Rotten
                    ),
                    Pair(
                        strings.defectCutCrack,
                        result.defect_summary.cutCrack
                    ),
                    Pair(
                        strings.defectSprouted,
                        result.defect_summary.Sprouted
                    ),
                    Pair(
                        strings.defectSkinDamage,
                        result.defect_summary.skinDamage
                    ),
                    Pair(
                        strings.defectSunburned,
                        result.defect_summary.Sunburned
                    ),
                    Pair(
                        strings.defectMisshapen,
                        result.defect_summary.Misshapen
                    )
                )

                val tableRect = RectF(
                    margin,
                    y,
                    pageWidth - margin,
                    y + 80f
                )

                bgPaint.color =
                    android.graphics.Color.WHITE

                canvas.drawRoundRect(
                    tableRect,
                    8f,
                    8f,
                    bgPaint
                )

                canvas.drawRoundRect(
                    tableRect,
                    8f,
                    8f,
                    strokePaint
                )

                val colW =
                    printableWidth / 3f

                defects.forEachIndexed { index, pair ->

                    val col =
                        index % 3

                    val row =
                        index / 3

                    val x =
                        margin +
                                16f +
                                col * colW

                    val textY =
                        y +
                                20f +
                                row * 26f

                    canvas.drawText(
                        "${pair.first}:",
                        x,
                        textY,
                        smallPaint
                    )

                    val countPaint =
                        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                            textSize = 11f
                            isFakeBoldText = true
                            color =
                                if (pair.second > 0)
                                    colorReject
                                else
                                    colorPurple
                        }

                    canvas.drawText(
                        "${pair.second}",
                        x + 85f,
                        textY,
                        countPaint
                    )
                }

                y += 100f

                // =========================================================
                // INDIVIDUAL RESULTS
                // =========================================================

                newPageIfNeeded(50f)

                canvas.drawText(
                    strings.individualResultsTitle,
                    margin,
                    y,
                    sectionTitlePaint
                )

                y += 18f

                // =========================================================
                // EACH ONION
                // =========================================================

                result.onions.forEachIndexed { index, onion ->

                    val cardHeight = 145f

                    newPageIfNeeded(cardHeight + 10f)

                    val cardRect = RectF(
                        margin,
                        y,
                        pageWidth - margin,
                        y + cardHeight
                    )

                    bgPaint.color = colorGrayLight

                    canvas.drawRoundRect(
                        cardRect,
                        10f,
                        10f,
                        bgPaint
                    )

                    canvas.drawRoundRect(
                        cardRect,
                        10f,
                        10f,
                        strokePaint
                    )

                    // -----------------------------------------------------
                    // CROP IMAGE
                    // -----------------------------------------------------

                    val cropRect = RectF(
                        margin + 10f,
                        y + 10f,
                        margin + 125f,
                        y + 135f
                    )

                    bgPaint.color =
                        android.graphics.Color.WHITE

                    canvas.drawRoundRect(
                        cropRect,
                        8f,
                        8f,
                        bgPaint
                    )

                    canvas.drawRoundRect(
                        cropRect,
                        8f,
                        8f,
                        strokePaint
                    )

                    val cropBitmap =
                        loadBitmap(onion.crop_url)

                    if (cropBitmap != null) {

                        drawBitmap(
                            cropBitmap,
                            RectF(
                                cropRect.left + 4f,
                                cropRect.top + 4f,
                                cropRect.right - 4f,
                                cropRect.bottom - 4f
                            )
                        )

                    } else {

                        val unavailablePaint =
                            TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                                textSize = 8f
                                color = colorGrayDark
                            }

                        canvas.drawText(
                            "Image unavailable",
                            cropRect.left + 12f,
                            cropRect.centerY(),
                            unavailablePaint
                        )
                    }

                    // -----------------------------------------------------
                    // ONION DETAILS
                    // -----------------------------------------------------

                    val contentX =
                        cropRect.right + 14f

                    val onionNum =
                        "Onion #${(index + 1)
                            .toString()
                            .padStart(2, '0')}"

                    canvas.drawText(
                        onionNum,
                        contentX,
                        y + 24f,
                        bodyBoldPaint
                    )

                    // Grade
                    val gradeDisplay =
                        AppStrings.translateGrade(
                            onion.grade,
                            strings
                        )

                    val gradeColor =
                        when {
                            onion.grade.equals(
                                "REJECT",
                                ignoreCase = true
                            ) -> colorReject

                            onion.grade.equals(
                                "Grade A",
                                ignoreCase = true
                            ) ||
                                    onion.grade.equals(
                                        "A",
                                        ignoreCase = true
                                    ) -> colorGradeA

                            else -> colorURS
                        }

                    val gradePaint =
                        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                            textSize = 10f
                            isFakeBoldText = true
                            color = gradeColor
                        }

                    canvas.drawText(
                        "Grade: $gradeDisplay",
                        contentX,
                        y + 42f,
                        gradePaint
                    )

                    // Dimensions
                    val dimensions =
                        "Diameter: ${onion.size.diameter_mm} mm"

                    canvas.drawText(
                        dimensions,
                        contentX,
                        y + 60f,
                        smallPaint
                    )

                    canvas.drawText(
                        "Width: ${onion.size.width_mm} mm",
                        contentX,
                        y + 75f,
                        smallPaint
                    )

                    canvas.drawText(
                        "Height: ${onion.size.height_mm} mm",
                        contentX,
                        y + 90f,
                        smallPaint
                    )

                    // Defects
                    val defectText =
                        if (onion.defects.isEmpty()) {

                            "${strings.classificationLabel}: " +
                                    strings.defectNoDefect

                        } else {

                            onion.defects.joinToString(", ") { defect ->

                                "${AppStrings.translateDefect(
                                    defect.name,
                                    strings
                                )} " +
                                        "(${(defect.confidence * 100).toInt()}%)"
                            }
                        }

                    canvas.drawText(
                        defectText,
                        contentX,
                        y + 108f,
                        smallPaint
                    )

                    // Reason
                    if (onion.grade_reason.isNotBlank()) {

                        val reason =
                            "Reason: ${onion.grade_reason}"

                        canvas.drawText(
                            reason,
                            contentX,
                            y + 124f,
                            smallPaint
                        )
                    }

                    y += cardHeight + 10f
                }

                // =========================================================
                // FINAL FOOTER
                // =========================================================

                drawFooter()

                document.finishPage(page)

                // =========================================================
                // SAVE
                // =========================================================

                val fileName =
                    "PyazLens_Report_${System.currentTimeMillis()}.pdf"

                val file =
                    File(
                        context.cacheDir,
                        fileName
                    )

                FileOutputStream(file).use { output ->

                    document.writeTo(output)
                }

                document.close()

                // =========================================================
                // SHARE
                // =========================================================

                val uri =
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )

                val shareIntent =
                    Intent(Intent.ACTION_SEND).apply {

                        type = "application/pdf"

                        putExtra(
                            Intent.EXTRA_STREAM,
                            uri
                        )

                        addFlags(
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }

                withContext(Dispatchers.Main) {
                    context.startActivity(
                        Intent.createChooser(
                            shareIntent,
                            "PyazLens Report PDF"
                        )
                    )
                }

            } catch (e: Exception) {
                Log.e("PdfReportGenerator", "Error generating PDF report", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Unable to generate PDF report: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}