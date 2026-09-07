package com.example.pyazlens.ui.language

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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.pyazlens.ui.splash.SplashScreen
import com.example.pyazlens.ui.theme.PyazLensTheme

private val Purple = Color(0xff4B1A4B)
private val LightPurple = Color(0xFFF5EFF6)
private val Green = Color(0xFF73C943)
private val BorderPurple = Color(0xFF6A2867)

@Composable
fun LanguageScreen(
    onContinue: (String) -> Unit
) {

    var selectedLanguage by remember {
        mutableStateOf("English")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCFAFD))
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {

        // --------------------------------
        // App branding
        // --------------------------------

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .offset(x = (-8).dp)
                    .clip(RoundedCornerShape(5.dp))
                    .offset(y = (6).dp)
                    .background(color = Color(0xFFFCFAFD)),
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

            Text(
                text = "PyazLens",
                color = Purple,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --------------------------------
        // Heading
        // --------------------------------

        Text(
            text = "Choose your\nlanguage",
            color = Purple,
            fontSize = 30.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select a language to continue",
            color = Color(0xFF6F6370),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // English

        LanguageOption(
            language = "English",
            subtitle = "Default application\nlanguage",
            code = "EN",
            selected = selectedLanguage == "English",
            onClick = {
                selectedLanguage = "English"
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // --------------------------------
        // Hindi
        // --------------------------------

        LanguageOption(
            language = "हिंदी",
            subtitle = "अनुप्रयोग भाषा हिन्दी में बदलें",
            code = "हि",
            selected = selectedLanguage == "Hindi",
            onClick = {
                selectedLanguage = "Hindi"
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // --------------------------------
        // Continue button
        // --------------------------------

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Purple)
                .clickable {
                    onContinue(selectedLanguage)
                },
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Continue →",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}


@Composable
private fun LanguageOption(
    language: String,
    subtitle: String,
    code: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(119.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(
                width = if (selected) 1.dp else 1.dp,
                color = if (selected) BorderPurple else Color(0xFFE9E3EA),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                onClick()
            }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Language code box
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (selected) LightPurple
                    else Color(0xFFFFF8F0)
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = code,
                color = if (selected) Purple else Color(0xFFF07820),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.size(14.dp))

        // Language text
        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = language,
                color = Color(0xFF2D252D),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                color = Color(0xFF8C828D),
                fontSize = 14.sp,
                lineHeight = 15.sp
            )
        }

        // Selection indicator
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (selected) Purple
                    else Color.White
                )
                .border(
                    width = if (selected) 0.dp else 1.dp,
                    color = Color(0xFFE9E3EA),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            if (selected) {
                Text(
                    text = "✓",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageScreenPreview() {
    PyazLensTheme {
        LanguageScreen(onContinue = {})
    }
}

