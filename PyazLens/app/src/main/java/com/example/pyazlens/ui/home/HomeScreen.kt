package com.example.pyazlens.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pyazlens.ui.theme.PyazLensTheme

private val Purple = Color(0xFF511D50)
private val Background = Color(0xFFFCFAFD)
private val Green = Color(0xFF73C943)
private val Gray = Color(0xFF8D8790)
private val BorderGray = Color(0xFFE9E5EA)

@Composable
fun HomeScreen(
    onInspectClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = 12.dp,
                bottom = 20.dp
            )
        ) {

            // ==========================================
            // TOP BAR
            // ==========================================

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // PyazLens logo
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Purple),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "◉",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = "PyazLens",
                        color = Purple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    // Notification
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(
                                width = 1.dp,
                                color = BorderGray,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Gray,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    // Profile
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE7E1E8)),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Purple,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(28.dp)
                )
            }


            // ==========================================
            // HEADING
            // ==========================================

            item {

                Text(
                    text = "Inspect Your Onions",
                    color = Purple,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "Use AI to analyze onion quality, size and",
                    color = Color(0xFF655D67),
                    fontSize = 10.sp
                )

                Text(
                    text = "defects.",
                    color = Color(0xFF655D67),
                    fontSize = 10.sp
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )
            }


            // ==========================================
            // INSPECT ONION CARD
            // ==========================================

            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(114.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Purple)
                        .clickable {
                            onInspectClick()
                        }
                        .padding(14.dp)
                ) {

                    Column {

                        // Camera icon
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(Green),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Inspect Onion",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        Text(
                            text = "Point your camera to start analysis",
                            color = Color(0xFFD9C9D8),
                            fontSize = 8.sp
                        )
                    }

                    // Decorative scan circle
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(38.dp)
                            .border(
                                width = 2.dp,
                                color = Color(0xFF86647F),
                                shape = CircleShape
                            )
                    )
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )
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
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "View History",
                        color = Green,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }


            // ==========================================
            // RECENT INSPECTION 1
            // ==========================================

            item {

                InspectionCard(
                    date = "12 SEP 2026 • 10:30",
                    result = "7 Onions Detected",
                    quality = "92/100 Quality",
                    size = "68mm Avg",
                    grade = "GRADE A"
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }


            // ==========================================
            // RECENT INSPECTION 2
            // ==========================================

            item {

                InspectionCard(
                    date = "11 SEP 2026 • 04:15",
                    result = "12 Onions Detected",
                    quality = "74/100 Quality",
                    size = "62mm Avg",
                    grade = "GRADE B"
                )
            }
        }
    }
}

@Composable
private fun InspectionCard(
    date: String,
    result: String,
    quality: String,
    size: String,
    grade: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFEAE6EB),
                shape = RoundedCornerShape(17.dp)
            )
            .padding(9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Temporary image area
        Box(
            modifier = Modifier
                .size(58.dp)
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
                text = date,
                color = Gray,
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = result,
                color = Purple,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Row {

                Text(
                    text = quality,
                    color = Color(0xFF66705E),
                    fontSize = 7.sp
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Text(
                    text = size,
                    color = Gray,
                    fontSize = 7.sp
                )
            }
        }

        // Grade
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(
                    if (grade == "GRADE A") {
                        Color(0xFFEAF7E5)
                    } else {
                        Color(0xFFFFEDE3)
                    }
                )
                .padding(
                    horizontal = 6.dp,
                    vertical = 4.dp
                )
        ) {

            Text(
                text = grade,
                color = if (grade == "GRADE A") {
                    Color(0xFF55A83A)
                } else {
                    Color(0xFFE36B35)
                },
                fontSize = 6.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PyazLensTheme {
        HomeScreen(
            onInspectClick = {}
        )
    }
}