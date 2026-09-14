package com.example.pyazlens.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pyazlens.data.language.AppStrings
import com.example.pyazlens.navigation.Screen
import com.example.pyazlens.ui.theme.PyazCardBorder
import com.example.pyazlens.ui.theme.PyazGreen
import com.example.pyazlens.ui.theme.PyazLensTheme
import com.example.pyazlens.ui.theme.PyazPurple

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    currentLanguage: String = "en",
    onItemClick: (String) -> Unit
) {
    val strings = AppStrings.getStrings(currentLanguage)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 12.dp,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. HOME
            BottomNavItem(
                icon = Icons.Default.Home,
                label = strings.navHome,
                route = Screen.Home.route,
                currentRoute = currentRoute,
                onClick = onItemClick
            )

            // 2. HISTORY
            BottomNavItem(
                icon = Icons.Default.History,
                label = strings.navHistory,
                route = Screen.History.route,
                currentRoute = currentRoute,
                onClick = onItemClick
            )

            // 3. SCAN (Center Prominent Circular Button)
            Column(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onItemClick(Screen.Scan.route) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .offset(y = (-6).dp)
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(PyazPurple, Color(0xFF3B123A))
                            )
                        )
                        .border(2.dp, PyazGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = strings.navScan,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = strings.navScan,
                    color = if (currentRoute == Screen.Scan.route) PyazPurple else Color(0xFF8A828F),
                    fontSize = 11.sp,
                    fontWeight = if (currentRoute == Screen.Scan.route) FontWeight.ExtraBold else FontWeight.Bold,
                    modifier = Modifier.offset(y = (-6).dp)
                )
            }

            // 4. INSIGHTS
            BottomNavItem(
                icon = Icons.Default.PieChart,
                label = strings.navInsights,
                route = Screen.Insights.route,
                currentRoute = currentRoute,
                onClick = onItemClick
            )

            // 5. SETTINGS
            BottomNavItem(
                icon = Icons.Default.Settings,
                label = strings.settingsTitle.uppercase(),
                route = Screen.Settings.route,
                currentRoute = currentRoute,
                onClick = onItemClick
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    route: String,
    currentRoute: String,
    onClick: (String) -> Unit
) {
    val selected = currentRoute == route

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Color(0xFFF3E8F5) else Color.Transparent)
            .clickable { onClick(route) }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) PyazPurple else Color(0xFF8A828F),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                color = if (selected) PyazPurple else Color(0xFF8A828F),
                fontSize = 10.sp,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    PyazLensTheme {
        BottomNavigationBar(
            currentRoute = "home",
            currentLanguage = "en",
            onItemClick = {}
        )
    }
}