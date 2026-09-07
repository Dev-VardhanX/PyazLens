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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.pyazlens.navigation.Screen
import com.example.pyazlens.ui.scan.ScanScreen
import com.example.pyazlens.ui.theme.PyazLensTheme

private val Purple = Color(0xFF511D50)
private val Background = Color(0xFFFCFAFD)
private val Green = Color(0xFF73C943)
private val Gray = Color(0xFF8D8790)
private val BorderGray = Color(0xFFE9E5EA)

@Composable
fun HomeScreen(
    onInspectClick: () -> Unit,
    onUploadClick: () -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        // TOP BAR

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // PyazLens logo
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .offset(x = (-8).dp)
                        .clip(RoundedCornerShape(5.dp))
                        .offset(y = (6).dp)
                        .background(color = Background),
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
                Row(){
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

                // Notification
                Box(
                    modifier = Modifier
                        .size(40.dp)
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
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                // Profile
                Box(
                    modifier = Modifier
                        .size(40.dp)
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
            // Greeting text
            Text(
                text ="Hello, dev! Ready to check your onions?",
                color = Purple,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp
            )
        }


//        // HEADING
//        item {
//
//            Text(
//                text = "Inspect Your Onions",
//                color = Purple,
//                fontSize = 30.sp,
//                fontWeight = FontWeight.ExtraBold
//            )
//
//            Spacer(
//                modifier = Modifier.height(5.dp)
//            )
//
//            Text(
//                text = "Use AI to analyze onion quality, size and\ndefects.",
//                color = Color(0xFF655D67),
//                fontSize = 14.sp,
//                lineHeight = 18.sp
//            )
//
//            Spacer(
//                modifier = Modifier.height(18.dp)
//            )
//        }




        // ==========================================
        // INSPECT ONION CARD
        // ==========================================

        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    //        .height(180.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Purple)
                    .padding(20.dp)
            ) {

                Column {

                    // Camera icon
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
        // SCAN ACTIONS (Camera vs Gallery)
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primary Action Button (Camera)
                Button(
                    onClick =
                        onInspectClick
                    ,
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

                // Secondary Action Button (Gallery)
                OutlinedButton(
                    onClick = onUploadClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Purple)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = null,
                            tint = Purple,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
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
                horizontalArrangement = Arrangement.SpaceBetween,
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
                    fontWeight = FontWeight.Bold
                )
            }
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
        item {

            InspectionCard(
                date = "11 SEP 2026 • 04:15",
                result = "12 Onions Detected",
                quality = "74/100 Quality",
                size = "62mm Avg",
                grade = "GRADE B"
            )
        }
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
            .height(100.dp)
            .clip(RoundedCornerShape(20.dp))
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
                text = date,
                color = Gray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = result,
                color = Purple,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Row {

                Text(
                    text = quality,
                    color = Color(0xFF66705E),
                    fontSize = 11.sp
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Text(
                    text = size,
                    color = Gray,
                    fontSize = 11.sp
                )
            }
        }

        // Grade
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
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
                fontSize = 10.sp,
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
            onInspectClick = {},
            onUploadClick = {}
        )
    }
}