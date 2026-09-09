package com.example.pyazlens.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pyazlens.R
import com.example.pyazlens.data.network.HistoryInspection
import com.example.pyazlens.data.network.RetrofitClient
import com.example.pyazlens.ui.theme.PyazLensTheme
import java.text.SimpleDateFormat
import java.util.Locale

private val Purple = Color(0xFF511D50)
private val Background = Color(0xFFFCFAFD)
private val Green = Color(0xFF73C943)
private val Gray = Color(0xFF8D8790)
private val BorderGray = Color(0xFFE9E5EA)

@Composable
fun HomeScreen(
    userName: String,
    userProfileId: Long,
    onInspectClick: () -> Unit,
    onUploadClick: () -> Unit,
    onSeeAllClick: () -> Unit
) {

    var recentInspections by remember {
        mutableStateOf<List<HistoryInspection>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(userProfileId) {

        try {

            val response =
                RetrofitClient.api.getUserInspections(
                    userProfileId
                )

            if (response.success) {

                recentInspections =
                    response.inspections
                        .take(3)
            }

        } catch (_: Exception) {

            // Keep the home screen usable if history
            // temporarily cannot be loaded.

        } finally {

            isLoading = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        // ==========================================
        // TOP BAR
        // ==========================================

        item {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .offset(x = (-8).dp)
                        .clip(RoundedCornerShape(5.dp))
                        .offset(y = 6.dp)
                        .background(Background),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = painterResource(
                            id = R.drawable.pyazlens_logo
                        ),
                        contentDescription = "PyazLens Logo",
                        modifier = Modifier
                            .size(42.dp)
                            .scale(4f)
                    )
                }

                Row {

                    Text(
                        text = "Pyaz",
                        color = Purple,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Lens",
                        color = Green,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(
                            1.dp,
                            BorderGray,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(
                            1.dp,
                            BorderGray,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Purple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "Hello, ${userName.ifBlank { "there" }}! Ready to check your onions?",
                color = Purple,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp
            )
        }

        // ==========================================
        // INSPECT ONION CARD
        // ==========================================

        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Purple)
                    .padding(20.dp)
            ) {

                Column {

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Green),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Text(
                        text = "Inspect Onion",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "Use AI to quickly detect visible defects and assess onion condition.",
                        color = Color(0xFFD9C9D8),
                        fontSize = 16.sp
                    )
                }
            }
        }

        // ==========================================
        // SCAN / UPLOAD
        // ==========================================

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    onClick = onInspectClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple,
                        contentColor = Color.White
                    )
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Scan an onion",
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = "Scan",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                OutlinedButton(
                    onClick = onUploadClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        Purple
                    )
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = null,
                            tint = Purple,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = "Upload",
                            color = Purple,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // ==========================================
        // RECENT INSPECTIONS HEADER
        // ==========================================

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Recent Inspections",
                    color = Purple,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "See all >",
                    color = Green,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        onSeeAllClick()
                    }
                )
            }
        }

        // ==========================================
        // RECENT INSPECTIONS
        // ==========================================

        if (isLoading) {

            item {

                Text(
                    text = "Loading recent inspections...",
                    color = Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(
                        vertical = 8.dp
                    )
                )
            }

        } else if (recentInspections.isEmpty()) {

            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(
                            1.dp,
                            BorderGray,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "🧅",
                            fontSize = 32.sp
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "No inspections yet",
                            color = Purple,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "Scan or upload an onion to get started.",
                            color = Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }

        } else {

            recentInspections.forEach { inspection ->

                item(
                    key = inspection.id
                ) {

                    InspectionCard(
                        inspection = inspection
                    )
                }
            }
        }

        item {

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }
    }
}

@Composable
private fun InspectionCard(
    inspection: HistoryInspection
) {

    val grade = inspection.final_grade
        ?.uppercase()
        ?.replace("GRADE ", "")
        ?: "—"

    val displayGrade =
        when (grade) {
            "A" -> "GRADE A"
            "URS" -> "URS"
            "REJECT" -> "REJECT"
            else -> grade
        }

    val gradeBackground =
        when (grade) {
            "A" -> Color(0xFFEAF7E5)
            "URS" -> Color(0xFFFFF4D9)
            "REJECT" -> Color(0xFFFFE3E3)
            else -> Color(0xFFF1EEF1)
        }

    val gradeColor =
        when (grade) {
            "A" -> Color(0xFF55A83A)
            "URS" -> Color(0xFFD49320)
            "REJECT" -> Color(0xFFD94A4A)
            else -> Gray
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFEAE6EB),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Visual placeholder only — inspection data itself is real.
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF0E5DF)),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "🧅",
                fontSize = 27.sp
            )
        }

        Spacer(
            modifier = Modifier.width(9.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = formatInspectionDate(
                    inspection.created_at
                ),
                color = Gray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "${inspection.total_onions} Onions Detected",
                color = Purple,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Row {

                inspection.rejected_percentage?.let {

                    Text(
                        text = "${it.toInt()}% rejected",
                        color = Color(0xFF66705E),
                        fontSize = 11.sp
                    )
                }

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                inspection.grade_a_percentage?.let {

                    Text(
                        text = "${it.toInt()}% Grade A",
                        color = Gray,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(gradeBackground)
                .padding(
                    horizontal = 6.dp,
                    vertical = 4.dp
                )
        ) {

            Text(
                text = displayGrade,
                color = gradeColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatInspectionDate(
    dateString: String?
): String {

    if (dateString.isNullOrBlank()) {
        return "DATE UNKNOWN"
    }

    return try {

        val inputFormat =
            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss",
                Locale.US
            )

        val outputFormat =
            SimpleDateFormat(
                "dd MMM yyyy • HH:mm",
                Locale.US
            )

        val date =
            inputFormat.parse(
                dateString.substringBefore(".")
            )

        if (date != null) {
            outputFormat.format(date).uppercase()
        } else {
            dateString
        }

    } catch (_: Exception) {

        dateString
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {

    PyazLensTheme {

        HomeScreen(
            userName = "Dev",
            userProfileId = 5L,
            onInspectClick = {},
            onUploadClick = {},
            onSeeAllClick = {}
        )
    }
}