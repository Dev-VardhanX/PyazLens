package com.example.pyazlens.ui.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pyazlens.data.language.AppStrings
import com.example.pyazlens.data.language.UiStrings
import com.example.pyazlens.data.network.AnalyzeResponse
import com.example.pyazlens.data.network.Defect
import com.example.pyazlens.data.network.DefectSummary
import com.example.pyazlens.data.network.Measurement
import com.example.pyazlens.data.network.OnionResult
import com.example.pyazlens.data.network.OnionSize
import com.example.pyazlens.data.network.Probabilities
import com.example.pyazlens.data.network.Summary
import com.example.pyazlens.data.pdf.PdfReportGenerator
import com.example.pyazlens.ui.theme.PyazBackground
import com.example.pyazlens.ui.theme.PyazCardBorder
import com.example.pyazlens.ui.theme.PyazGradeA
import com.example.pyazlens.ui.theme.PyazGray
import com.example.pyazlens.ui.theme.PyazGreen
import com.example.pyazlens.ui.theme.PyazLensTheme
import com.example.pyazlens.ui.theme.PyazPurple
import com.example.pyazlens.ui.theme.PyazReject
import com.example.pyazlens.ui.theme.PyazURS
import kotlin.math.roundToInt
import android.net.Uri
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.input.pointer.pointerInput
import coil.compose.AsyncImage
import android.graphics.BitmapFactory
import androidx.compose.runtime.LaunchedEffect
import android.graphics.Bitmap
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.asImageBitmap
import com.example.pyazlens.data.network.RetrofitClient
import coil.request.ImageRequest
import coil.request.CachePolicy
import com.example.pyazlens.data.network.RetrofitClient.imageUrl

@Composable
fun InspectionResultScreen(
    result: AnalyzeResponse,
    imageUri: Uri?,
    imageUrl: String? = null,
    currentLanguage: String = "en",
    onDone: () -> Unit = {}
) {
    val context = LocalContext.current
    val strings = AppStrings.getStrings(currentLanguage)

    // Determine overall batch outcome grade
    val (overallGradeRaw, heroOutcomeText, heroColor, heroBg, heroBorder, heroIcon) = when {
        result.summary.rejected > 0 -> Tuple6(
            "REJECT",
            strings.heroRejectOutcome,
            PyazReject,
            Color(0xFFFFF0F0),
            Color(0xFFFFCACA),
            Icons.Default.Warning
        )
        result.summary.grade_urs > 0 -> Tuple6(
            "URS",
            strings.heroUrsOutcome,
            PyazURS,
            Color(0xFFFFF9E6),
            Color(0xFFFFE8A3),
            Icons.Default.Warning
        )
        else -> Tuple6(
            "Grade A",
            strings.heroGradeAOutcome,
            PyazGradeA,
            Color(0xFFEFF9EC),
            Color(0xFFC3E8B6),
            Icons.Default.CheckCircle
        )
    }

    val displayOverallGrade = AppStrings.translateGrade(overallGradeRaw, strings)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PyazBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. TOP HEADER & BAR
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDone,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = PyazPurple
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.inspectionResultTitle,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PyazPurple
                    )
                    val countSub = try {
                        String.format(strings.onionsAnalyzedSub, result.total_onions)
                    } catch (_: Exception) {
                        "${result.total_onions} Onions Analyzed"
                    }
                    Text(
                        text = countSub,
                        fontSize = 13.sp,
                        color = PyazGray,
                        fontWeight = FontWeight.Medium
                    )
                }

                // PDF Action Button
                OutlinedButton(
                    onClick = {
                        PdfReportGenerator.generateAndShareReport(
                            context = context,
                            result = result,
                            currentLanguage = currentLanguage,
                            imageUri = imageUri,
                            imageUrl = imageUrl
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PyazPurple.copy(alpha = 0.4f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PyazPurple)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = strings.downloadPdfBtn,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PDF",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

//        // 2. HERO OVERALL OUTCOME CARD (2-Second Visual Hierarchy)
//        item {
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(24.dp),
//                colors = CardDefaults.cardColors(containerColor = heroBg),
//                border = BorderStroke(1.5.dp, heroBorder),
//                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(20.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    // Status Badge Pill
//                    Box(
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(20.dp))
//                            .background(Color.White.copy(alpha = 0.9f))
//                            .padding(horizontal = 12.dp, vertical = 4.dp)
//                    ) {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Box(
//                                modifier = Modifier
//                                    .size(8.dp)
//                                    .clip(CircleShape)
//                                    .background(heroColor)
//                            )
//                            Spacer(modifier = Modifier.width(6.dp))
//                            Text(
//                                text = strings.inspectionCompleteBadge,
//                                fontSize = 11.sp,
//                                fontWeight = FontWeight.ExtraBold,
//                                color = PyazPurple,
//                                letterSpacing = 0.5.sp
//                            )
//                        }
//                    }
//
//                    Spacer(modifier = Modifier.height(14.dp))
//
//                    // Primary Hero Grade Title
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        Icon(
//                            imageVector = heroIcon,
//                            contentDescription = null,
//                            tint = heroColor,
//                            modifier = Modifier.size(32.dp)
//                        )
//                        Spacer(modifier = Modifier.width(10.dp))
//                        Text(
//                            text = displayOverallGrade,
//                            fontSize = 32.sp,
//                            fontWeight = FontWeight.Black,
//                            color = heroColor,
//                            letterSpacing = 0.5.sp
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(4.dp))
//
//                    // Hero Subtitle / Outcome Summary
//                    Text(
//                        text = heroOutcomeText,
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF332938),
//                        textAlign = TextAlign.Center
//                    )
//
//                    Spacer(modifier = Modifier.height(14.dp))
//
//                    // Analyzed Batch Quick Pill
//                    Box(
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(12.dp))
//                            .background(Color.White)
//                            .padding(horizontal = 14.dp, vertical = 6.dp)
//                    ) {
//                        Text(
//                            text = "${result.total_onions} ${strings.onionLabel} (${result.summary.grade_a} ${strings.gradeA} • ${result.summary.grade_urs} URS • ${result.summary.rejected} ${strings.gradeReject})",
//                            fontSize = 12.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = PyazPurple
//                        )
//                    }
//                }
//            }
//        }

        // 2. AI VISUAL INSPECTION
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(
                    1.dp,
                    PyazCardBorder
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {

                    Text(
                        text = "AI Visual Inspection",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PyazPurple
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Tap an onion to view its detailed result",
                        fontSize = 12.sp,
                        color = PyazGray,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                    OnionInspectionViewer(
                        imageUri = imageUri,
                        imageUrl = imageUrl,
                        onions = result.onions,
                        onOnionClick = { onion ->

                            // For now, expand the matching onion card.
                            // Individual detail navigation comes next.
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(PyazGradeA)
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                text = "Grade A",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PyazPurple
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(PyazURS)
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                text = "URS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PyazPurple
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(PyazReject)
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                text = "Reject",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PyazPurple
                            )
                        }
                    }
                }
            }
        }
        // 2. HERO OVERALL OUTCOME CARD
        item {
            val totalOnions = result.total_onions.coerceAtLeast(1)

            val gradeACount = result.summary.grade_a
            val gradeUrsCount = result.summary.grade_urs
            val rejectedCount = result.summary.rejected

            val gradeAPercentage = gradeACount * 100f / totalOnions
            val gradeUrsPercentage = gradeUrsCount * 100f / totalOnions
            val rejectedPercentage = rejectedCount * 100f / totalOnions

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = heroBg
                ),
                border = BorderStroke(
                    1.5.dp,
                    heroBorder
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Status Badge
//                    Box(
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(20.dp))
//                            .background(
//                                Color.White.copy(alpha = 0.9f)
//                            )
//                            .padding(
//                                horizontal = 12.dp,
//                                vertical = 4.dp
//                            )
//                    ) {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Box(
//                                modifier = Modifier
//                                    .size(8.dp)
//                                    .clip(CircleShape)
//                                    .background(heroColor)
//                            )
//
//                            Spacer(
//                                modifier = Modifier.width(6.dp)
//                            )
//
//                            Text(
//                                text = strings.inspectionCompleteBadge,
//                                fontSize = 11.sp,
//                                fontWeight = FontWeight.ExtraBold,
//                                color = PyazPurple,
//                                letterSpacing = 0.5.sp
//                            )
//                        }
//                    }

//                    Spacer(
//                        modifier = Modifier.height(14.dp)
//                    )

                    // Overall Grade
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        Icon(
//                            imageVector = heroIcon,
//                            contentDescription = null,
//                            tint = heroColor,
//                            modifier = Modifier.size(30.dp)
//                        )
//
//                        Spacer(
//                            modifier = Modifier.width(9.dp)
//                        )
//
//                        Text(
//                            text = displayOverallGrade,
//                            fontSize = 30.sp,
//                            fontWeight = FontWeight.Black,
//                            color = heroColor
//                        )
//                    }
//
//                    Spacer(
//                        modifier = Modifier.height(4.dp)
//                    )
//
//                    Text(
//                        text = heroOutcomeText,
//                        fontSize = 15.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF332938),
//                        textAlign = TextAlign.Center
//                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    // Circular Batch Composition
                    BatchCompositionCircle(
                        gradeAPercentage = gradeAPercentage,
                        gradeUrsPercentage = gradeUrsPercentage,
                        rejectedPercentage = rejectedPercentage
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    // Percentage Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        BatchLegendItem(
                            percentage = gradeAPercentage,
                            label = "Grade A",
                            color = PyazGradeA
                        )

                        BatchLegendItem(
                            percentage = gradeUrsPercentage,
                            label = "URS",
                            color = PyazURS
                        )

                        BatchLegendItem(
                            percentage = rejectedPercentage,
                            label = "Rejected",
                            color = PyazReject
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )
                }
            }
        }
        // 2. HERO OVERALL OUTCOME CARD item { val totalOnions = result.total_onions.coerceAtLeast(1) val gradeACount = result.summary.grade_a val gradeUrsCount = result.summary.grade_urs val rejectedCount = result.summary.rejected val gradeAPercentage = gradeACount * 100f / totalOnions val gradeUrsPercentage = gradeUrsCount * 100f / totalOnions val rejectedPercentage = rejectedCount * 100f / totalOnions Card( modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = heroBg), border = BorderStroke(1.5.dp, heroBorder), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) ) { Column( modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally ) { // Status Badge Box( modifier = Modifier .clip(RoundedCornerShape(20.dp)) .background(Color.White.copy(alpha = 0.9f)) .padding(horizontal = 12.dp, vertical = 4.dp) ) { Row( verticalAlignment = Alignment.CenterVertically ) { Box( modifier = Modifier .size(8.dp) .clip(CircleShape) .background(heroColor) ) Spacer(modifier = Modifier.width(6.dp)) Text( text = strings.inspectionCompleteBadge, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = PyazPurple, letterSpacing = 0.5.sp ) } } Spacer(modifier = Modifier.height(14.dp)) // Overall Grade Row( verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center ) { Icon( imageVector = heroIcon, contentDescription = null, tint = heroColor, modifier = Modifier.size(30.dp) ) Spacer(modifier = Modifier.width(9.dp)) Text( text = displayOverallGrade, fontSize = 30.sp, fontWeight = FontWeight.Black, color = heroColor ) } Spacer(modifier = Modifier.height(4.dp)) Text( text = heroOutcomeText, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF332938), textAlign = TextAlign.Center ) Spacer(modifier = Modifier.height(18.dp)) // Circular Batch Composition BatchCompositionCircle( gradeAPercentage = gradeAPercentage, gradeUrsPercentage = gradeUrsPercentage, rejectedPercentage = rejectedPercentage ) Spacer(modifier = Modifier.height(16.dp)) // Percentage Legend Row( modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly ) { BatchLegendItem( percentage = gradeAPercentage, label = "Good", color = Color(0xFF2E7D32) ) BatchLegendItem( percentage = gradeUrsPercentage, label = "URS", color = Color(0xFFF57C00) ) BatchLegendItem( percentage = rejectedPercentage, label = "Rejected", color = Color(0xFFC62828) ) } Spacer(modifier = Modifier.height(14.dp)) // Total inspected Box( modifier = Modifier .clip(RoundedCornerShape(12.dp)) .background(Color.White) .padding(horizontal = 14.dp, vertical = 6.dp) ) { Text( text = "${result.total_onions} ${strings.onionLabel} inspected", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PyazPurple ) } } } }
        // 3. CLEAN SUMMARY METRICS GRID
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryMetricPill(
                    modifier = Modifier.weight(1f),
                    label = strings.summaryTotalLabel,
                    value = result.total_onions.toString(),
                    subText = "100%",
                    color = PyazPurple,
                    bgColor = Color.White
                )

                SummaryMetricPill(
                    modifier = Modifier.weight(1f),
                    label = strings.gradeA,
                    value = result.summary.grade_a.toString(),
                    subText = "${result.summary.grade_a_percentage.toInt()}%",
                    color = PyazGradeA,
                    bgColor = Color(0xFFEAF7E5)
                )

                SummaryMetricPill(
                    modifier = Modifier.weight(1f),
                    label = strings.gradeUrs,
                    value = result.summary.grade_urs.toString(),
                    subText = "${result.summary.grade_urs_percentage.toInt()}%",
                    color = PyazURS,
                    bgColor = Color(0xFFFFF4D9)
                )

                SummaryMetricPill(
                    modifier = Modifier.weight(1f),
                    label = strings.gradeReject,
                    value = result.summary.rejected.toString(),
                    subText = "${result.summary.rejected_percentage.toInt()}%",
                    color = PyazReject,
                    bgColor = Color(0xFFFFE3E3)
                )
            }
        }

        // 4. "WHY THIS GRADE?" QUALITY EXPLANATION
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, PyazCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Box(
//                            modifier = Modifier
//                                .size(32.dp)
//                                .clip(RoundedCornerShape(8.dp))
//                                .background(Color(0xFFF3E8F5)),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.HelpOutline,
//                                contentDescription = null,
//                                tint = PyazPurple,
//                                modifier = Modifier.size(18.dp)
//                            )
//                        }
//                        Spacer(modifier = Modifier.width(10.dp))
//                        Text(
//                            text = strings.whyThisGradeHeader,
//                            fontSize = 17.sp,
//                            fontWeight = FontWeight.ExtraBold,
//                            color = PyazPurple
//                        )
//                    }

                    Spacer(modifier = Modifier.height(0.dp))

                    // Concise reasoning bullets based on actual results
                    val primaryReason = buildGradeExplanation(result, strings)
//                    Text(
//                        text = primaryReason,
//                        fontSize = 13.sp,
//                        color = Color(0xFF3B3340),
//                        lineHeight = 18.sp,
//                        fontWeight = FontWeight.Medium
//                    )

                   // Spacer(modifier = Modifier.height(14.dp))

                    // Standard Criteria Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CriteriaChip(
                            modifier = Modifier.weight(1f),
                            title = "Grade A",
                            desc = "≥50mm • 0 Flaws",
                            color = PyazGradeA
                        )
                        CriteriaChip(
                            modifier = Modifier.weight(1f),
                            title = "URS",
                            desc = "40-50mm / Minor",
                            color = PyazURS
                        )
                        CriteriaChip(
                            modifier = Modifier.weight(1f),
                            title = "REJECT",
                            desc = "Rot/Sprout/Cut",
                            color = PyazReject
                        )
                    }
                }
            }
        }

        // 5. DEFECT SUMMARY GRID (Detected vs Zero Flaws)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, PyazCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.defectSummaryTitle,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PyazPurple
                        )

                        val totalDefectsFound = with(result.defect_summary) {
                            Rotten + cutCrack + Sprouted + skinDamage + Sunburned + Misshapen
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (totalDefectsFound > 0) Color(0xFFFFE3E3) else Color(0xFFEAF7E5))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (totalDefectsFound > 0) "$totalDefectsFound Detected" else "0 Defects",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (totalDefectsFound > 0) PyazReject else PyazGradeA
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val defectList = listOf(
                        Pair(strings.defectRotten, result.defect_summary.Rotten),
                        Pair(strings.defectCutCrack, result.defect_summary.cutCrack),
                        Pair(strings.defectSprouted, result.defect_summary.Sprouted),
                        Pair(strings.defectSkinDamage, result.defect_summary.skinDamage),
                        Pair(strings.defectSunburned, result.defect_summary.Sunburned),
                        Pair(strings.defectMisshapen, result.defect_summary.Misshapen)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in defectList.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                VisualDefectChip(
                                    modifier = Modifier.weight(1f),
                                    name = defectList[i].first,
                                    count = defectList[i].second
                                )
                                if (i + 1 < defectList.size) {
                                    VisualDefectChip(
                                        modifier = Modifier.weight(1f),
                                        name = defectList[i + 1].first,
                                        count = defectList[i + 1].second
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. SIZE & PHYSICAL ANALYSIS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, PyazCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.sizeAnalysisHeader,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PyazPurple
                        )

                        Text(
                            text = strings.refCoinCalibrated,
                            fontSize = 11.sp,
                            color = PyazGray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val avgDiameter = if (result.onions.isNotEmpty()) result.onions.map { it.size.diameter_mm }.average() else 0.0
                    val avgWidth = if (result.onions.isNotEmpty()) result.onions.map { it.size.width_mm }.average() else 0.0
                    val avgHeight = if (result.onions.isNotEmpty()) result.onions.map { it.size.height_mm }.average() else 0.0

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DimensionMetricCard(
                            modifier = Modifier.weight(1f),
                            label = strings.averageDiameter,
                            value = "${String.format("%.1f", avgDiameter)} mm"
                        )
                        DimensionMetricCard(
                            modifier = Modifier.weight(1f),
                            label = strings.averageWidth,
                            value = "${String.format("%.1f", avgWidth)} mm"
                        )
                        DimensionMetricCard(
                            modifier = Modifier.weight(1f),
                            label = strings.averageHeight,
                            value = "${String.format("%.1f", avgHeight)} mm"
                        )
                    }
                }
            }
        }

        // 7. INDIVIDUAL ONIONS SECTION HEADER
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${strings.individualResultsTitle} (${result.onions.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PyazPurple
            )
        }

        // 8. INDIVIDUAL ONION CARDS
        itemsIndexed(result.onions) { index, onion ->
            OnionExpandableCard(
                onion = onion,
                onionNumber = index + 1,
                strings = strings
            )
        }

        // 9. BOTTOM ACTIONS (New Inspection & PDF)
        item {
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Primary Action: New Inspection
                Button(
                    onClick = onDone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PyazPurple),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.newInspectionBtn,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Secondary Action: Download PDF Report
                OutlinedButton(
                    onClick = {
                        PdfReportGenerator.generateAndShareReport(
                            context = context,
                            result = result,
                            currentLanguage = currentLanguage
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, PyazPurple.copy(alpha = 0.4f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PyazPurple)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.downloadPdfBtn,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// =========================================================
// HELPER COMPONENTS
// =========================================================

private data class Tuple6<A, B, C, D, E, F>(
    val a: A, val b: B, val c: C, val d: D, val e: E, val f: F
)

@Composable
private fun SummaryMetricPill(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    subText: String,
    color: Color,
    bgColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PyazPurple,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subText,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
private fun CriteriaChip(
    modifier: Modifier = Modifier,
    title: String,
    desc: String,
    color: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, fontSize = 9.sp, color = PyazGray, fontWeight = FontWeight.Medium, maxLines = 1)
        }
    }
}

@Composable
private fun VisualDefectChip(
    modifier: Modifier = Modifier,
    name: String,
    count: Int
) {
    val isDetected = count > 0
    val bgColor = if (isDetected) Color(0xFFFFE3E3) else Color(0xFFF9F8FA)
    val textColor = if (isDetected) PyazReject else PyazGray
    val countBg = if (isDetected) PyazReject else Color(0xFFEFECEF)
    val countTextColor = if (isDetected) Color.White else PyazPurple

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = if (isDetected) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(countBg)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = count.toString(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = countTextColor
                )
            }
        }
    }
}

@Composable
private fun DimensionMetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF6F3F7))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 10.sp, color = PyazGray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 13.sp, color = PyazPurple, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun OnionExpandableCard(
    onion: OnionResult,
    onionNumber: Int,
    strings: UiStrings
) {
    var isExpanded by remember { mutableStateOf(true) }
    val imageCacheKey = remember {
        System.currentTimeMillis()
    }
    val isRejected =
        onion.grade.equals("REJECT", ignoreCase = true) ||
                onion.grade.equals("REJECTED", ignoreCase = true)

    val isGradeA =
        onion.grade.equals("Grade A", ignoreCase = true) ||
                onion.grade.equals("A", ignoreCase = true)

    val gradeColor = when {
        isRejected -> PyazReject
        isGradeA -> PyazGradeA
        else -> PyazURS
    }

    val gradeBg = when {
        isRejected -> Color(0xFFFFE3E3)
        isGradeA -> Color(0xFFEAF7E5)
        else -> Color(0xFFFFF4D9)
    }

    val displayGrade =
        AppStrings.translateGrade(onion.grade, strings)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            1.dp,
            PyazCardBorder
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            // =====================================================
            // HEADER
            // =====================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isExpanded = !isExpanded
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "${strings.onionLabel} ${
                            onionNumber
                                .toString()
                                .padStart(2, '0')
                        }",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PyazPurple
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Icon(
                        imageVector =
                            if (isExpanded)
                                Icons.Default.ExpandLess
                            else
                                Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = PyazGray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Grade badge
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                        .background(gradeBg)
                        .padding(
                            horizontal = 11.dp,
                            vertical = 5.dp
                        )
                ) {
                    Text(
                        text = displayGrade,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = gradeColor
                    )
                }
            }

            // =====================================================
            // DETAILS
            // =====================================================

            AnimatedVisibility(
                visible = isExpanded
            ) {

                Column {

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    // =================================================
                    // MAIN CONTENT
                    // IMAGE LEFT + DEFECTS RIGHT
                    // =================================================

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp),
                        verticalAlignment =
                            Alignment.Top
                    ) {

                        // ---------------------------------------------
                        // CROPPED IMAGE
                        // ---------------------------------------------

                        Card(
                            modifier = Modifier
                                .weight(0.9f)
                                .aspectRatio(0.92f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    Color(0xFFF6F3F7)
                            ),
                            elevation =
                                CardDefaults.cardElevation(
                                    defaultElevation = 0.dp
                                )
                        ) {

                            if (onion.crop_url.isNotBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(
                                            RetrofitClient.imageUrl(
                                                onion.crop_url
                                            )
                                        )
                                        .memoryCachePolicy(CachePolicy.DISABLED)
                                        .diskCachePolicy(CachePolicy.DISABLED)
                                        .build(),
                                    contentDescription = "Onion #$onionNumber",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(
                                            RoundedCornerShape(16.dp)
                                        ),
                                    contentScale =
                                        androidx.compose.ui.layout.ContentScale.Fit
                                )

                            } else {

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Color(0xFFF6F3F7)
                                        ),
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Text(
                                        text = "Image unavailable",
                                        fontSize = 11.sp,
                                        color = PyazGray,
                                        textAlign =
                                            TextAlign.Center
                                    )
                                }
                            }
                        }

                        // ---------------------------------------------
                        // DEFECTS
                        // ---------------------------------------------

                        Column(
                            modifier = Modifier.weight(1.1f)
                        ) {

                            Text(
                                text = "Defects",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PyazPurple
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            if (onion.defects.isEmpty()) {

                                Row(
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                PyazGradeA
                                            )
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(7.dp)
                                    )

                                    Text(
                                        text =
                                            strings.noDefectsDetected,
                                        fontSize = 12.sp,
                                        fontWeight =
                                            FontWeight.SemiBold,
                                        color = PyazGreen
                                    )
                                }

                            } else {

                                Column(
                                    verticalArrangement =
                                        Arrangement.spacedBy(7.dp)
                                ) {

                                    onion.defects.forEach { defect ->

                                        val translatedDefect =
                                            AppStrings.translateDefect(
                                                defect.name,
                                                strings
                                            )

                                        Row(
                                            verticalAlignment =
                                                Alignment.Top
                                        ) {

                                            Text(
                                                text = "•",
                                                fontSize = 14.sp,
                                                fontWeight =
                                                    FontWeight.Bold,
                                                color = PyazReject
                                            )

                                            Spacer(
                                                modifier =
                                                    Modifier.width(5.dp)
                                            )

                                            Column {

                                                Text(
                                                    text =
                                                        translatedDefect,
                                                    fontSize = 12.sp,
                                                    fontWeight =
                                                        FontWeight.Bold,
                                                    color =
                                                        Color(0xFF332938)
                                                )

                                                Text(
                                                    text =
                                                        "${(defect.confidence * 100).toInt()}% confidence",
                                                    fontSize = 10.sp,
                                                    color =
                                                        PyazGray
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    // =================================================
                    // SIZE METRICS
                    // =================================================

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        OnionMeasurementBox(
                            modifier = Modifier.weight(1f),
                            label = strings.diameterLabel,
                            value =
                                "${String.format(
                                    "%.1f",
                                    onion.size.diameter_mm
                                )} mm"
                        )

                        OnionMeasurementBox(
                            modifier = Modifier.weight(1f),
                            label = strings.widthLabel,
                            value =
                                "${String.format(
                                    "%.1f",
                                    onion.size.width_mm
                                )} mm"
                        )

                        OnionMeasurementBox(
                            modifier = Modifier.weight(1f),
                            label = strings.heightLabel,
                            value =
                                "${String.format(
                                    "%.1f",
                                    onion.size.height_mm
                                )} mm"
                        )
                    }

                    // =================================================
                    // GRADE REASON
                    // =================================================

                    if (onion.grade_reason.isNotBlank()) {

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(
                                    RoundedCornerShape(10.dp)
                                )
                                .background(
                                    Color(0xFFF7F5F8)
                                )
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 8.dp
                                )
                        ) {

                            Column {

                                Text(
                                    text = "Why this grade?",
                                    fontSize = 11.sp,
                                    fontWeight =
                                        FontWeight.ExtraBold,
                                    color = PyazPurple
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(3.dp)
                                )

                                Text(
                                    text = onion.grade_reason,
                                    fontSize = 11.sp,
                                    color = PyazGray,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnionMeasurementBox(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF6F3F7))
            .padding(
                horizontal = 6.dp,
                vertical = 8.dp
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = PyazGray
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PyazPurple
            )
        }
    }
}

@Composable
private fun SizeChip(modifier: Modifier = Modifier, label: String, value: String) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF6F3F7))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 10.sp, color = PyazGray, fontWeight = FontWeight.Bold)
            Text(text = value, fontSize = 12.sp, color = PyazPurple, fontWeight = FontWeight.ExtraBold)
        }
    }
}

private fun buildGradeExplanation(result: AnalyzeResponse, strings: UiStrings): String {
    val total = result.total_onions
    val rejections = result.summary.rejected
    val urs = result.summary.grade_urs
    val gradeA = result.summary.grade_a

    return when {
        rejections > 0 -> {
            val rejectedReasons = result.onions.filter {
                it.grade.equals("REJECT", ignoreCase = true) || it.grade.equals("REJECTED", ignoreCase = true)
            }.mapNotNull { it.grade_reason.takeIf { r -> r.isNotBlank() } }.distinct()

            if (rejectedReasons.isNotEmpty()) {
                "• $rejections of $total onion(s) rejected: ${rejectedReasons.joinToString("; ")}"
            } else {
                "• $rejections of $total onion(s) rejected due to critical flaws (Rotten, Sprouted, or Cut/Crack)."
            }
        }
        urs > 0 -> {
            "• $urs of $total onion(s) graded URS due to minor non-critical defects or smaller physical size."
        }
        else -> {
            "• All $total onion(s) meet Grade A standards with optimal physical dimensions and zero detected flaws."
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InspectionResultScreenPreview() {
    val sampleResult = AnalyzeResponse(
        success = true,
        user_profile_id = 1L,
        total_onions = 3,
        measurement = Measurement(reference_coin_diameter_mm = 24.26, mm_per_pixel = 0.5),
        summary = Summary(
            grade_a = 1,
            grade_urs = 1,
            rejected = 1,
            grade_a_percentage = 33.3,
            grade_urs_percentage = 33.3,
            rejected_percentage = 33.3
        ),
        defect_summary = DefectSummary(
            Rotten = 1,
            cutCrack = 0,
            Sprouted = 0,
            skinDamage = 1,
            Sunburned = 0,
            Misshapen = 0
        ),
        onions = listOf(
            OnionResult(
                id = 1,
                size = OnionSize(55.0, 56.0, 54.0),
                classification = "Good",
                defects = emptyList(),
                probabilities = Probabilities(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                grade = "Grade A",
                grade_reason = "Size is adequate, no defects."
            ),
            OnionResult(
                id = 2,
                size = OnionSize(45.0, 46.0, 44.0),
                classification = "Fair",
                defects = listOf(Defect("Skin Damage", 0.85)),
                probabilities = Probabilities(0.0, 0.0, 0.0, 0.85, 0.0, 0.0),
                grade = "URS",
                grade_reason = "Slightly smaller, minor skin damage."
            ),
            OnionResult(
                id = 3,
                size = OnionSize(60.0, 61.0, 59.0),
                classification = "Bad",
                defects = listOf(Defect("Rotten", 0.95)),
                probabilities = Probabilities(0.95, 0.0, 0.0, 0.0, 0.0, 0.0),
                grade = "REJECT",
                grade_reason = "Critical defect: Rotten."
            )
        )
    )

    PyazLensTheme {
        InspectionResultScreen(
            result = sampleResult,
            currentLanguage = "en",
            onDone = {},
            imageUri = null
        )
    }
}
@Composable
private fun BatchCompositionCircle(
    gradeAPercentage: Float,
    gradeUrsPercentage: Float,
    rejectedPercentage: Float
) {
    val goodColor = Color(0xFF2E7D32)
    val ursColor = Color(0xFFF57C00)
    val rejectedColor = Color(0xFFC62828)

    Box(
        modifier = Modifier.size(170.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokeWidth = 18.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset(
                (size.width - diameter) / 2,
                (size.height - diameter) / 2
            )

            var startAngle = -90f

            val segments = listOf(
                gradeAPercentage to goodColor,
                gradeUrsPercentage to ursColor,
                rejectedPercentage to rejectedColor
            )

            segments.forEach { (percentage, color) ->

                val sweepAngle = percentage / 100f * 360f

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(diameter, diameter),
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )

                startAngle += sweepAngle
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${gradeAPercentage.roundToInt()}%",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF2E7D32)
            )

            Text(
                text = "Good",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF665D68)
            )
        }
    }
}

@Composable
private fun BatchLegendItem(
    percentage: Float,
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )

        Spacer(modifier = Modifier.width(5.dp))

        Column {
            Text(
                text = "${percentage.roundToInt()}%",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF332938)
            )

            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF665D68)
            )
        }
    }
}
@Composable
fun OnionInspectionViewer(
    imageUri: Uri?,
    imageUrl: String?,
    onions: List<OnionResult>,
    onOnionClick: (OnionResult) -> Unit
) {
    if (imageUri == null && imageUrl.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF6F3F7)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No image available",
                color = PyazGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        return
    }

    val context = LocalContext.current

    var bitmap by remember(imageUri, imageUrl) {
        mutableStateOf<Bitmap?>(null)
    }

    var imageLoadFailed by remember(imageUri, imageUrl) {
        mutableStateOf(false)
    }

    LaunchedEffect(imageUri, imageUrl) {

        bitmap = null
        imageLoadFailed = false

        try {

            // =====================================================
            // LOCAL IMAGE — FRESH INSPECTION
            // =====================================================

            if (imageUri != null) {

                val loadedBitmap =
                    context.contentResolver
                        .openInputStream(imageUri)
                        ?.use { inputStream ->
                            BitmapFactory.decodeStream(inputStream)
                        }

                if (loadedBitmap != null) {
                    bitmap = loadedBitmap
                } else {
                    imageLoadFailed = true
                }

            }

            // =====================================================
            // REMOTE IMAGE — HISTORY / HOME
            // =====================================================

            else if (!imageUrl.isNullOrBlank()) {

                val imageLoader =
                    coil.ImageLoader(context)

                val request =
                    ImageRequest.Builder(context)
                        .data(imageUrl)
                        .allowHardware(false)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .build()

                val result =
                    imageLoader.execute(request)

                val drawable = result.drawable

                if (drawable is android.graphics.drawable.BitmapDrawable) {
                    bitmap = drawable.bitmap
                } else {
                    imageLoadFailed = true
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()
            imageLoadFailed = true
        }
    }

    if (imageLoadFailed) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF6F3F7)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Unable to load image",
                    color = PyazReject,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = imageUrl ?: imageUri.toString(),
                    color = PyazGray,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        return
    }

    val loadedBitmap = bitmap

    if (loadedBitmap == null) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF6F3F7)),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Loading inspection image...",
                color = PyazGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        return
    }

    val imageWidth = loadedBitmap.width
    val imageHeight = loadedBitmap.height

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(
                imageWidth.toFloat() / imageHeight.toFloat()
            )
            .clip(RoundedCornerShape(16.dp))
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(
                    onions,
                    imageWidth,
                    imageHeight
                ) {

                    detectTapGestures { tapOffset ->

                        val scaleX =
                            size.width / imageWidth.toFloat()

                        val scaleY =
                            size.height / imageHeight.toFloat()

                        var selectedOnion: OnionResult? = null
                        var bestDistance = Float.MAX_VALUE

                        onions.forEach { onion ->

                            val points = onion.segmentation

                            if (points.size < 3) {
                                return@forEach
                            }

                            val centerX =
                                points
                                    .map { it[0] }
                                    .average()
                                    .toFloat()

                            val centerY =
                                points
                                    .map { it[1] }
                                    .average()
                                    .toFloat()

                            val scaledX =
                                centerX * scaleX

                            val scaledY =
                                centerY * scaleY

                            val dx =
                                tapOffset.x - scaledX

                            val dy =
                                tapOffset.y - scaledY

                            val distance =
                                (dx * dx) + (dy * dy)

                            if (distance < bestDistance) {
                                bestDistance = distance
                                selectedOnion = onion
                            }
                        }

                        selectedOnion?.let {
                            onOnionClick(it)
                        }
                    }
                }
        ) {

            drawImage(
                image = loadedBitmap.asImageBitmap(),
                dstSize = androidx.compose.ui.unit.IntSize(
                    width = size.width.roundToInt(),
                    height = size.height.roundToInt()
                )
            )

            val scaleX =
                size.width / imageWidth.toFloat()

            val scaleY =
                size.height / imageHeight.toFloat()

            onions.forEach { onion ->

                val points = onion.segmentation

                if (points.size < 3) {
                    return@forEach
                }

                val path =
                    androidx.compose.ui.graphics.Path()

                points.forEachIndexed { index, point ->

                    if (point.size < 2) {
                        return@forEachIndexed
                    }

                    val x =
                        point[0] * scaleX

                    val y =
                        point[1] * scaleY

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }

                path.close()

                val gradeColor =
                    when {
                        onion.grade.equals(
                            "A",
                            ignoreCase = true
                        ) -> PyazGradeA

                        onion.grade.equals(
                            "Grade A",
                            ignoreCase = true
                        ) -> PyazGradeA

                        onion.grade.contains(
                            "URS",
                            ignoreCase = true
                        ) -> PyazURS

                        else -> PyazReject
                    }

                drawPath(
                    path = path,
                    color = gradeColor.copy(alpha = 0.25f)
                )

                drawPath(
                    path = path,
                    color = gradeColor,
                    style = Stroke(
                        width = 4f
                    )
                )
            }
        }
    }
}