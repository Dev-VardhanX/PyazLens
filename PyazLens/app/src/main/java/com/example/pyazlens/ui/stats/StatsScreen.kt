package com.example.pyazlens.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pyazlens.data.network.HistoryInspection
import com.example.pyazlens.data.network.RetrofitClient

private val Purple = Color(0xFF511D50)
private val Background = Color(0xFFFCFAFD)
private val Green = Color(0xFF73C943)
private val Gray = Color(0xFF8D8790)
private val BorderGray = Color(0xFFEAE6EB)

@Composable
fun StatsScreen(
    userProfileId: Long
) {

    var inspections by remember {
        mutableStateOf<List<HistoryInspection>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(userProfileId) {

        try {

            val response =
                RetrofitClient.api.getUserInspections(
                    userProfileId
                )

            if (response.success) {

                inspections =
                    response.inspections

            } else {

                error = "Unable to load statistics."
            }

        } catch (e: Exception) {

            error =
                "Failed to load statistics: ${e.message}"

        } finally {

            isLoading = false
        }
    }

    val totalInspections =
        inspections.size

    val totalOnions =
        inspections.sumOf {
            it.total_onions
        }

    val gradeA =
        inspections.sumOf {
            it.grade_a_count ?: 0
        }

    val urs =
        inspections.sumOf {
            it.urs_count ?: 0
        }

    val rejected =
        inspections.sumOf {
            it.rejected_count ?: 0
        }

    val rotten =
        inspections.sumOf {
            it.rotten_count ?: 0
        }

    val sprouted =
        inspections.sumOf {
            it.sprouted_count ?: 0
        }

    val cutCrack =
        inspections.sumOf {
            it.cut_crack_count ?: 0
        }

    val skinDamage =
        inspections.sumOf {
            it.skin_damage_count ?: 0
        }

    val sunburned =
        inspections.sumOf {
            it.sunburned_count ?: 0
        }

    val misshapen =
        inspections.sumOf {
            it.misshapen_count ?: 0
        }

    val gradeAPercentage =
        percentage(
            gradeA,
            totalOnions
        )

    val ursPercentage =
        percentage(
            urs,
            totalOnions
        )

    val rejectedPercentage =
        percentage(
            rejected,
            totalOnions
        )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 16.dp),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        item {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Your Statistics",
                color = Purple,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text =
                    "A complete overview of your onion inspections.",
                color = Gray,
                fontSize = 14.sp
            )
        }

        if (isLoading) {

            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color = Purple
                    )
                }
            }

        } else if (error != null) {

            item {

                Text(
                    text = error!!,
                    color = Color(0xFFD94A4A),
                    fontSize = 14.sp
                )
            }

        } else if (inspections.isEmpty()) {

            item {

                EmptyStatsCard()
            }

        } else {

            // ==================================================
            // OVERVIEW
            // ==================================================

            item {

                StatsSectionTitle(
                    "Overview"
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    StatCard(
                        modifier =
                            Modifier.weight(1f),
                        icon =
                            Icons.Default.Analytics,
                        title =
                            "Inspections",
                        value =
                            totalInspections.toString()
                    )

                    StatCard(
                        modifier =
                            Modifier.weight(1f),
                        icon =
                            Icons.Default.CheckCircle,
                        title =
                            "Onions Analyzed",
                        value =
                            totalOnions.toString()
                    )
                }
            }

            // ==================================================
            // QUALITY
            // ==================================================

            item {

                StatsSectionTitle(
                    "Quality Distribution"
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                QualityRow(
                    title = "Grade A",
                    count = gradeA,
                    percentage = gradeAPercentage,
                    icon = Icons.Default.CheckCircle,
                    iconColor = Green
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                QualityRow(
                    title = "Grade URS",
                    count = urs,
                    percentage = ursPercentage,
                    icon = Icons.Default.Warning,
                    iconColor = Color(0xFFD49320)
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                QualityRow(
                    title = "Rejected",
                    count = rejected,
                    percentage = rejectedPercentage,
                    icon = Icons.Default.Close,
                    iconColor = Color(0xFFD94A4A)
                )
            }

            // ==================================================
            // DEFECTS
            // ==================================================

            item {

                StatsSectionTitle(
                    "Defect Analysis"
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                DefectRow(
                    "Rotten",
                    rotten
                )

                DefectRow(
                    "Sprouted",
                    sprouted
                )

                DefectRow(
                    "Cut / Crack",
                    cutCrack
                )

                DefectRow(
                    "Skin Damage",
                    skinDamage
                )

                DefectRow(
                    "Sunburned",
                    sunburned
                )

                DefectRow(
                    "Misshapen",
                    misshapen
                )
            }

            item {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }
        }
    }
}

@Composable
private fun StatsSectionTitle(
    text: String
) {

    Text(
        text = text,
        color = Purple,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
private fun StatCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    value: String
) {

    Box(
        modifier = modifier
            .height(125.dp)
            .background(
                Color.White,
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                BorderGray,
                RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {

        Column {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Purple,
                modifier = Modifier.size(25.dp)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = value,
                color = Purple,
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = title,
                color = Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun QualityRow(
    title: String,
    count: Int,
    percentage: Double,
    icon: ImageVector,
    iconColor: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White,
                RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                BorderGray,
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(
            modifier = Modifier.size(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = Purple,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "$count onions",
                color = Gray,
                fontSize = 12.sp
            )
        }

        Text(
            text = "${percentage.toInt()}%",
            color = Purple,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun DefectRow(
    title: String,
    count: Int
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 7.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = title,
            color = Purple,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = count.toString(),
            color = Purple,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptyStatsCard() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White,
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                BorderGray,
                RoundedCornerShape(20.dp)
            )
            .padding(30.dp),
        contentAlignment =
            Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "📊",
                fontSize = 36.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "No statistics yet",
                color = Purple,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text =
                    "Complete an inspection to start building your statistics.",
                color = Gray,
                fontSize = 13.sp
            )
        }
    }
}

private fun percentage(
    value: Int,
    total: Int
): Double {

    if (total <= 0) {
        return 0.0
    }

    return value * 100.0 / total
}