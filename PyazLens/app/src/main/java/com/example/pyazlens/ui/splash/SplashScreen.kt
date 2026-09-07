package com.example.pyazlens.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pyazlens.R
import com.example.pyazlens.ui.theme.PyazLensTheme
import kotlinx.coroutines.delay
import kotlin.math.round

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {

    LaunchedEffect(Unit) {
        delay(2500)
        onSplashFinished()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff4B1A4B))
            .windowInsetsPadding(WindowInsets.safeDrawing),
        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(
                id = R.drawable.pyazlenslogo
            ),
            contentDescription = "PyazLens Logo",
            modifier = Modifier.size(180.dp)
                .clip(RoundedCornerShape(40.dp))
        )

        Spacer(modifier = Modifier.height(28.dp))
        Row() {
            Text(
                text = "Pyaz",
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Lens",
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6CC24A)
            )
        }


        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "AI-powered Onion Quality",
            color = Color(0xFFD0B8CF),
            fontSize = 15.sp
        )

        Text(
            text = "Assessment",
            color = Color(0xFFD0B8CF),
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(100.dp))

        Text(
            text = "• • •",
            color = Color(0xFF6CC24A),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "INITIALIZING AI ENGINE",
            color = Color(0xFFB58AB2),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    PyazLensTheme {
        SplashScreen({})
    }
}