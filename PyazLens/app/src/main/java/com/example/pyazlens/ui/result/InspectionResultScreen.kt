package com.example.pyazlens.ui.result

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pyazlens.data.network.AnalyzeResponse
import com.example.pyazlens.data.network.OnionResult

private val Purple = Color(0xFF511D50)
private val Green = Color(0xFF73C943)
private val LightBackground = Color(0xFFF7F4F7)

@Composable
fun InspectionResultScreen(
    result: AnalyzeResponse
) {

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(20.dp)
    ) {

        item {

            Text(
                text = "Inspection Result",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Purple
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "${result.total_onions} onions analyzed",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // =================================================
            // DOWNLOAD PDF BUTTON
            // =================================================

            Button(
                onClick = {
                    generateInspectionPdf(
                        context = context,
                        result = result
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple
                )
            ) {

                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download PDF"
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Download PDF Report",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }

        // =================================================
        // SUMMARY
        // =================================================

        item {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Overall Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Purple
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        SummaryItem(
                            title = "Grade A",
                            count = result.summary.grade_a,
                            percentage =
                                result.summary.grade_a_percentage
                        )

                        SummaryItem(
                            title = "URS",
                            count = result.summary.grade_urs,
                            percentage =
                                result.summary.grade_urs_percentage
                        )

                        SummaryItem(
                            title = "Rejected",
                            count = result.summary.rejected,
                            percentage =
                                result.summary.rejected_percentage
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }

        // =================================================
        // DEFECT SUMMARY
        // =================================================

        item {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Defect Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Purple
                    )

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    DefectRow(
                        name = "Rotten",
                        count = result.defect_summary.Rotten
                    )

                    DefectRow(
                        name = "Cut / Crack",
                        count = result.defect_summary.cutCrack
                    )

                    DefectRow(
                        name = "Sprouted",
                        count = result.defect_summary.Sprouted
                    )

                    DefectRow(
                        name = "Skin Damage",
                        count = result.defect_summary.skinDamage
                    )

                    DefectRow(
                        name = "Sunburned",
                        count = result.defect_summary.Sunburned
                    )

                    DefectRow(
                        name = "Misshapen",
                        count = result.defect_summary.Misshapen
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }

        // =================================================
        // INDIVIDUAL ONIONS
        // =================================================

        item {

            Text(
                text = "Individual Onion Results",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Purple
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }

        items(result.onions) { onion ->

            val onionNumber = result.onions.indexOf(onion) + 1

            OnionResultCard(
                onion = onion,
                onionNumber = onionNumber
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        item {

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}


// =================================================
// SUMMARY ITEM
// =================================================

@Composable
private fun SummaryItem(
    title: String,
    count: Int,
    percentage: Double
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = count.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Purple
        )

        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "${percentage}%",
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}


// =================================================
// DEFECT ROW
// =================================================

@Composable
private fun DefectRow(
    name: String,
    count: Int
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),

        horizontalArrangement =
            Arrangement.SpaceBetween,

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = name,
            fontSize = 14.sp
        )

        Text(
            text = count.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Purple
        )
    }
}


// =================================================
// ONION RESULT CARD
// =================================================

@Composable
private fun OnionResultCard(
    onion: OnionResult,
    onionNumber: Int
) {

    val isRejected =
        onion.grade == "REJECT"

    val isGradeA =
        onion.grade == "Grade A"

    val gradeColor =
        when {
            isRejected -> Color(0xFFC62828)
            isGradeA -> Green
            else -> Purple
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "Onion #${onionNumber.toString().padStart(2, '0')}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Purple
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            if (isRejected)
                                Icons.Default.Warning
                            else
                                Icons.Default.CheckCircle,

                        contentDescription = null,

                        tint = gradeColor,

                        modifier =
                            Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier = Modifier.size(5.dp)
                    )

                    Text(
                        text = onion.grade,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = gradeColor
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text =
                    "Diameter: ${onion.size.diameter_mm} mm",
                fontSize = 14.sp
            )

            Text(
                text =
                    "Width: ${onion.size.width_mm} mm",
                fontSize = 14.sp
            )

            Text(
                text =
                    "Height: ${onion.size.height_mm} mm",
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text =
                    "Classification: ${onion.classification}",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            if (onion.defects.isEmpty()) {

                Text(
                    text = "No defects detected",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Green
                )

            } else {

                Text(
                    text = "Detected defects:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                onion.defects.forEach { defect ->

                    Text(
                        text =
                            "• ${defect.name} " +
                                    "(${(defect.confidence * 100).toInt()}%)",

                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = onion.grade_reason,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Start
            )
        }
    }
}


// =================================================
// PDF GENERATION
// =================================================

private fun generateInspectionPdf(
    context: Context,
    result: AnalyzeResponse
) {
    try {
        val document = PdfDocument()

        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f

        var pageNumber = 1
        var page = document.startPage(
            PdfDocument.PageInfo.Builder(
                pageWidth,
                pageHeight,
                pageNumber
            ).create()
        )

        var canvas = page.canvas
        var y = 50f

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 26f
            isFakeBoldText = true
            color = android.graphics.Color.BLACK
        }

        val headingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 17f
            isFakeBoldText = true
            color = android.graphics.Color.BLACK
        }

        val normalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 13f
            isFakeBoldText = false
            color = android.graphics.Color.BLACK
        }

        val smallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 11f
            isFakeBoldText = false
            color = android.graphics.Color.DKGRAY
        }

        fun newPageIfNeeded() {
            if (y > pageHeight - 60f) {
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
                y = 50f
            }
        }

        fun text(value: String, paint: Paint, spacing: Float = 20f) {
            newPageIfNeeded()

            canvas.drawText(
                value,
                margin,
                y,
                paint
            )

            y += spacing
        }

        // =================================================
        // HEADER
        // =================================================

        text(
            "PYAZLENS",
            titlePaint,
            35f
        )

        text(
            "Onion Quality Inspection Report",
            headingPaint,
            30f
        )

        y += 10f

        // =================================================
        // INSPECTION SUMMARY
        // =================================================

        text(
            "Inspection Summary",
            headingPaint,
            25f
        )

        text(
            "Total onions analyzed: ${result.total_onions}",
            normalPaint
        )

        text(
            "Grade A: ${result.summary.grade_a} " +
                    "(${result.summary.grade_a_percentage}%)",
            normalPaint
        )

        text(
            "URS: ${result.summary.grade_urs} " +
                    "(${result.summary.grade_urs_percentage}%)",
            normalPaint
        )

        text(
            "Rejected: ${result.summary.rejected} " +
                    "(${result.summary.rejected_percentage}%)",
            normalPaint
        )

        y += 10f

        // =================================================
        // DEFECT SUMMARY
        // =================================================

        text(
            "Defect Summary",
            headingPaint,
            25f
        )

        text(
            "Rotten: ${result.defect_summary.Rotten}",
            normalPaint
        )

        text(
            "Cut / Crack: ${result.defect_summary.cutCrack}",
            normalPaint
        )

        text(
            "Sprouted: ${result.defect_summary.Sprouted}",
            normalPaint
        )

        text(
            "Skin Damage: ${result.defect_summary.skinDamage}",
            normalPaint
        )

        text(
            "Sunburned: ${result.defect_summary.Sunburned}",
            normalPaint
        )

        text(
            "Misshapen: ${result.defect_summary.Misshapen}",
            normalPaint
        )

        y += 10f

        // =================================================
        // INDIVIDUAL RESULTS
        // =================================================

        text(
            "Individual Onion Results",
            headingPaint,
            25f
        )

        result.onions.forEachIndexed { index, onion ->

            newPageIfNeeded()

            text(
                "Onion #${(index + 1).toString().padStart(2, '0')}",
                headingPaint,
                23f
            )

            text(
                "Grade: ${onion.grade}",
                normalPaint
            )

            text(
                "Diameter: ${onion.size.diameter_mm} mm",
                normalPaint
            )

            text(
                "Width: ${onion.size.width_mm} mm",
                normalPaint
            )

            text(
                "Height: ${onion.size.height_mm} mm",
                normalPaint
            )

            text(
                "Classification: ${onion.classification}",
                normalPaint
            )

            if (onion.defects.isEmpty()) {

                text(
                    "Defects: None",
                    normalPaint
                )

            } else {

                text(
                    "Detected defects:",
                    normalPaint
                )

                onion.defects.forEach { defect ->

                    text(
                        "- ${defect.name} " +
                                "(${(defect.confidence * 100).toInt()}%)",
                        smallPaint
                    )
                }
            }

            text(
                "Reason: ${onion.grade_reason}",
                smallPaint,
                25f
            )

            y += 8f
        }

        // Finish final page
        document.finishPage(page)

        // =================================================
        // SAVE PDF
        // =================================================

        val fileName =
            "PyazLens_Inspection_${System.currentTimeMillis()}.pdf"

        val file = java.io.File(
            context.cacheDir,
            fileName
        )

        java.io.FileOutputStream(file).use { output ->
            document.writeTo(output)
        }

        document.close()

        // =================================================
        // SHARE / SAVE
        // =================================================

        val uri =
            androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {

            type = "application/pdf"

            putExtra(
                Intent.EXTRA_STREAM,
                uri
            )

            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        context.startActivity(
            Intent.createChooser(
                shareIntent,
                "Save or share inspection report"
            )
        )

    } catch (exception: Exception) {

        exception.printStackTrace()

        Toast.makeText(
            context,
            "Unable to create PDF report",
            Toast.LENGTH_LONG
        ).show()
    }
}