package com.example.pyazlens.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pyazlens.navigation.Screen
import com.example.pyazlens.ui.theme.PyazLensTheme

private val Purple = Color(0xFF511D50)
private val Gray = Color(0xFF9CA0A8)

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onItemClick: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .windowInsetsPadding(
                WindowInsets.navigationBars
            )
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Home
        BottomNavItem(
            icon = Icons.Default.Home,
            label = "HOME",
            route = "home",
            currentRoute = currentRoute,
            onClick = onItemClick
        )
        //History
        BottomNavItem(
            icon = Icons.Default.History,
            label = "HISTORY",
            route = "history",
            currentRoute = currentRoute,
            onClick = onItemClick
        )

        // 3. SCAN (Center Prominent Circular Button)
        Column(
            modifier = Modifier.clickable { onItemClick(Screen.Scan.route) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .offset(y = (-12).dp)
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Purple),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Scan",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Scan",
                color = if (currentRoute == Screen.Scan.route) Purple else Color(0xFF9CA0A8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(y = (-8).dp)
            )
        }

        // 4. INSIGHTS
        BottomNavItem(
            icon = Icons.Default.PieChart,
            label = "Insight",
            route = Screen.Insights.route,
            currentRoute = currentRoute,
            onClick = onItemClick
        )

        BottomNavItem(
            icon = Icons.Default.Settings,
            label = "SETTINGS",
            route = "settings",
            currentRoute = currentRoute,
            onClick = onItemClick
        )
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

    Column(
        modifier = Modifier
            .clickable {
                onClick(route)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Purple else Gray,
            modifier = Modifier.size(26.dp)
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = label,
            color = if (selected) Purple else Gray,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    PyazLensTheme {
        BottomNavigationBar(
            currentRoute = "home",
            onItemClick = {}
        )
    }
}