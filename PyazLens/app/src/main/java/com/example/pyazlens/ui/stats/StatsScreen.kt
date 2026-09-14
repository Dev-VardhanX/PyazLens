package com.example.pyazlens.ui.stats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pyazlens.data.language.AppStrings
import com.example.pyazlens.data.language.UiStrings
import com.example.pyazlens.data.network.HistoryInspection
import com.example.pyazlens.data.network.RetrofitClient
import com.example.pyazlens.ui.theme.PyazBackground
import com.example.pyazlens.ui.theme.PyazCardBorder
import com.example.pyazlens.ui.theme.PyazGradeA
import com.example.pyazlens.ui.theme.PyazGray
import com.example.pyazlens.ui.theme.PyazGreen
import com.example.pyazlens.ui.theme.PyazLensTheme
import com.example.pyazlens.ui.theme.PyazPurple
import com.example.pyazlens.ui.theme.PyazReject
import com.example.pyazlens.ui.theme.PyazURS

@Composable
fun StatsScreen(
    userProfileId: Long,
    currentLanguage: String = "en"
) {
    val strings = AppStrings.getStrings(currentLanguage)

    var inspections by remember { mutableStateOf<List<HistoryInspection>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userProfileId) {
        try {
            val response = RetrofitClient.api.getUserInspections(userProfileId)
            if (response.success) {
                inspections = response.inspections
            } else {
                error = strings.noStatsYetTitle
            }
        } catch (e: Exception) {
            error = "${strings.connectionFailed}: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    val totalInspections = inspections.size
    val totalOnions = inspections.sumOf { it.total_onions }
    val gradeA = inspections.sumOf { it.grade_a_count ?: 0 }
    val urs = inspections.sumOf { it.urs_count ?: 0 }
    val rejected = inspections.sumOf { it.rejected_count ?: 0 }

    val rotten = inspections.sumOf { it.rotten_count ?: 0 }
    val sprouted = inspections.sumOf { it.sprouted_count ?: 0 }
    val cutCrack = inspections.sumOf { it.cut_crack_count ?: 0 }
    val skinDamage = inspections.sumOf { it.skin_damage_count ?: 0 }
    val sunburned = inspections.sumOf { it.sunburned_count ?: 0 }
    val misshapen = inspections.sumOf { it.misshapen_count ?: 0 }

    val totalDefects = rotten + sprouted + cutCrack + skinDamage + sunburned + misshapen

    val gradeAPercentage = percentage(gradeA, totalOnions)
    val ursPercentage = percentage(urs, totalOnions)
    val rejectedPercentage = percentage(rejected, totalOnions)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PyazBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. TOP TITLE
        item {
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PieChart,
                    contentDescription = null,
                    tint = PyazPurple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.yourStatsTitle,
                    color = PyazPurple,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = strings.statsSub,
                color = PyazGray,
                fontSize = 13.sp
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = PyazPurple, strokeWidth = 3.dp, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = strings.loadingInspections, color = PyazGray, fontSize = 14.sp)
                    }
                }
            }
        } else if (error != null && totalInspections == 0) {
            item {
                Text(
                    text = error!!,
                    color = PyazReject,
                    fontSize = 14.sp
                )
            }
        } else if (inspections.isEmpty()) {
            item {
                EmptyStatsCard(strings = strings)
            }
        } else {
            // OVERVIEW HERO CARDS
            item {
                StatsSectionTitle(strings.overviewTitle)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Analytics,
                        title = strings.totalInspectionsCard,
                        value = totalInspections.toString(),
                        bgColor = Color(0xFFF3E8F5),
                        accentColor = PyazPurple
                    )

                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CheckCircle,
                        title = strings.onionsAnalyzedCard,
                        value = totalOnions.toString(),
                        bgColor = Color(0xFFEAF7E5),
                        accentColor = PyazGradeA
                    )
                }
            }

            // QUALITY DISTRIBUTION
            item {
                StatsSectionTitle(strings.qualityDistTitle)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, PyazCardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        QualityRow(
                            title = strings.gradeA,
                            count = gradeA,
                            percentage = gradeAPercentage,
                            icon = Icons.Default.CheckCircle,
                            iconColor = PyazGradeA,
                            onionLabel = strings.onionLabel,
                            barColor = PyazGradeA
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        QualityRow(
                            title = strings.gradeUrs,
                            count = urs,
                            percentage = ursPercentage,
                            icon = Icons.Default.Warning,
                            iconColor = PyazURS,
                            onionLabel = strings.onionLabel,
                            barColor = PyazURS
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        QualityRow(
                            title = strings.gradeReject,
                            count = rejected,
                            percentage = rejectedPercentage,
                            icon = Icons.Default.Close,
                            iconColor = PyazReject,
                            onionLabel = strings.onionLabel,
                            barColor = PyazReject
                        )
                    }
                }
            }

            // DEFECT FREQUENCY ANALYSIS
            item {
                StatsSectionTitle(strings.defectAnalysisTitle)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, PyazCardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DefectStatBar(name = strings.defectRotten, count = rotten, totalDefects = totalDefects)
                        DefectStatBar(name = strings.defectSprouted, count = sprouted, totalDefects = totalDefects)
                        DefectStatBar(name = strings.defectCutCrack, count = cutCrack, totalDefects = totalDefects)
                        DefectStatBar(name = strings.defectSkinDamage, count = skinDamage, totalDefects = totalDefects)
                        DefectStatBar(name = strings.defectSunburned, count = sunburned, totalDefects = totalDefects)
                        DefectStatBar(name = strings.defectMisshapen, count = misshapen, totalDefects = totalDefects)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StatsSectionTitle(text: String) {
    Text(
        text = text,
        color = PyazPurple,
        fontSize = 17.sp,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
private fun StatCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    bgColor: Color,
    accentColor: Color
) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, PyazCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PyazPurple
                )
            }

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PyazGray
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
    iconColor: Color,
    onionLabel: String,
    barColor: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = PyazPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$count $onionLabel",
                    color = PyazGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${percentage.toInt()}%",
                    color = iconColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Visual Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF3EEF4))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = (percentage / 100.0).toFloat().coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
private fun DefectStatBar(
    name: String,
    count: Int,
    totalDefects: Int
) {
    val percent = if (totalDefects > 0) (count.toDouble() / totalDefects * 100).toInt() else 0

    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, fontSize = 13.sp, color = Color(0xFF3B3340), fontWeight = FontWeight.Medium)
            Text(text = "$count ($percent%)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (count > 0) PyazReject else PyazPurple)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFFF3EEF4))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = (percent / 100.0).toFloat().coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (count > 0) PyazReject else PyazPurple.copy(alpha = 0.2f))
            )
        }
    }
}

@Composable
private fun EmptyStatsCard(strings: UiStrings) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, PyazCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "📊", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = strings.noStatsYetTitle,
                color = PyazPurple,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = strings.noStatsYetSub,
                color = PyazGray,
                fontSize = 13.sp
            )
        }
    }
}

private fun percentage(part: Int, total: Int): Double {
    if (total == 0) return 0.0
    return (part.toDouble() / total.toDouble()) * 100.0
}

@Preview(showBackground = true)
@Composable
fun StatsScreenPreview() {
    PyazLensTheme {
        StatsScreen(
            userProfileId = 1L,
            currentLanguage = "en"
        )
    }
}
