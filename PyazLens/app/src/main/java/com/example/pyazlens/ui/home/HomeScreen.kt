package com.example.pyazlens.ui.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Book
import com.example.pyazlens.ui.settings.OnionInspectionGuideDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pyazlens.R
import com.example.pyazlens.data.language.AppStrings
import com.example.pyazlens.data.network.HistoryInspection
import com.example.pyazlens.data.network.RetrofitClient
import com.example.pyazlens.ui.history.HistoryItemCard
import com.example.pyazlens.ui.theme.PyazBackground
import com.example.pyazlens.ui.theme.PyazCardBorder
import com.example.pyazlens.ui.theme.PyazGray
import com.example.pyazlens.ui.theme.PyazGreen
import com.example.pyazlens.ui.theme.PyazLensTheme
import com.example.pyazlens.ui.theme.PyazPurple
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(
    userName: String,
    userProfileId: Long,
    currentLanguage: String = "en",
    onInspectClick: () -> Unit,
    onUploadClick: () -> Unit,
    onSeeAllClick: () -> Unit,
    onInspectionClick: (Int) -> Unit,
    onProfileClick: () -> Unit = {}
) {
    val strings = AppStrings.getStrings(currentLanguage)

    var recentInspections by remember { mutableStateOf<List<HistoryInspection>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showGuideDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userProfileId) {
        try {
            val response = RetrofitClient.api.getUserInspections(userProfileId)
            if (response.success) {
                recentInspections = response.inspections
            }
        } catch (_: Exception) {
            // Keep home usable offline
        } finally {
            isLoading = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PyazBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. TOP HEADER & PROFILE BAR
        item {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PyazBackground)
                            .offset(y =6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pyazlens_logo),
                            contentDescription = "PyazLens Logo",
                            modifier = Modifier.size(34.dp).scale(3.7f)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row {
                            Text(text = "Pyaz", color = PyazPurple, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                            Text(text = "Lens", color = PyazGreen, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Profile Avatar Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.5.dp, PyazPurple.copy(alpha = 0.3f), CircleShape)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = strings.navProfile,
                        tint = PyazPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // 2. GREETING CARD
        item {
            val greetingText = try {
                String.format(strings.helloGreeting, userName.ifBlank { "User" })
            } catch (_: Exception) {
                "Hello, ${userName.ifBlank { "User" }}!"
            }

            Column {
                Text(
                    text = greetingText,
                    color = PyazPurple,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp
                )
            }
        }

        // 3. HERO INSPECTION ACTION CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(PyazPurple, Color(0xFF381037))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(PyazGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = strings.inspectOnionTitle,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = if (currentLanguage == "hi") "तत्काल एआई विश्लेषण" else "Instant AI Grade & Defect Check",
                                    color = PyazGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = strings.inspectOnionSub,
                            color = Color(0xFFE4D8E6),
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Dual Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onInspectClick,
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PyazGreen,
                                    contentColor = Color.White
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = strings.scanBtn,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = strings.scanBtn,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = onUploadClick,
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.8f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Collections,
                                        contentDescription = strings.uploadBtn,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = strings.uploadBtn,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. ₹10 COIN MANDATORY PREPARATION CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2F8)),
                border = BorderStroke(1.dp, PyazPurple.copy(alpha = 0.22f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(PyazPurple.copy(alpha = 0.10f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🪙", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.coinBeforeScanHeader,
                                color = PyazPurple.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = strings.coinRequiredTitle,
                                color = PyazPurple,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = strings.coinRequiredDesc,
                                color = Color(0xFF4A3E4E),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Subtle Action Button to Open Full Guide
                    OutlinedButton(
                        onClick = { showGuideDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, PyazPurple.copy(alpha = 0.30f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PyazPurple,
                            containerColor = Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Book,
                                contentDescription = strings.viewInspectionGuideBtn,
                                tint = PyazPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.viewInspectionGuideBtn,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 5. RECENT INSPECTIONS HEADER
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = strings.recentInspections,
                        tint = PyazPurple,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.recentInspections,
                        color = PyazPurple,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = strings.seeAll,
                    color = PyazGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSeeAllClick() }
                )
            }
        }

        // 6. RECENT INSPECTIONS LIST
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = PyazPurple,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = strings.loadingRecent, color = PyazGray, fontSize = 14.sp)
                    }
                }
            }
        } else if (recentInspections.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, PyazCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🧅",
                            fontSize = 42.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = strings.noInspectionsYet,
                            color = PyazPurple,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = strings.noInspectionsSub,
                            color = PyazGray,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            recentInspections.take(3).forEach { inspection ->
                item(key = inspection.id) {

                    val inspectionNumber =
                        recentInspections.size -
                                recentInspections.indexOfFirst { it.id == inspection.id }

                    HistoryItemCard(
                        record = inspection,
                        inspectionNumber = inspectionNumber,
                        currentLanguage = currentLanguage,
                        onClick = {
                            onInspectionClick(inspection.id)
                        }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showGuideDialog) {
        OnionInspectionGuideDialog(
            currentLanguage = currentLanguage,
            onDismiss = { showGuideDialog = false }
        )
    }
}

private fun formatInspectionDate(dateString: String?): String {
    if (dateString.isNullOrBlank()) {
        return "DATE UNKNOWN"
    }

    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val outputFormat = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.US)
        val date = inputFormat.parse(dateString.substringBefore("."))
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
            currentLanguage = "en",
            onInspectClick = {},
            onUploadClick = {},
            onSeeAllClick = {},
            onInspectionClick = {}
        )
    }
}
