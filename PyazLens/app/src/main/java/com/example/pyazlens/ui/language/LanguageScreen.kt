package com.example.pyazlens.ui.language

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.pyazlens.data.language.AppStrings
import com.example.pyazlens.data.language.LanguageManager
import com.example.pyazlens.ui.theme.PyazBackground
import com.example.pyazlens.ui.theme.PyazCardBorder
import com.example.pyazlens.ui.theme.PyazLensTheme
import com.example.pyazlens.ui.theme.PyazPurple

@Composable
fun LanguageScreen(
    currentLanguage: String = "en",
    onContinue: (String) -> Unit
) {
    var selectedLanguageCode by remember {
        mutableStateOf(if (currentLanguage == LanguageManager.LANG_HINDI) LanguageManager.LANG_HINDI else LanguageManager.LANG_ENGLISH)
    }

    val strings = AppStrings.getStrings(selectedLanguageCode)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PyazBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        // App branding
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .offset(x = (-8).dp)
                    .clip(RoundedCornerShape(5.dp))
                    .offset(y = 6.dp)
                    .background(PyazBackground),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pyazlens_logo),
                    contentDescription = "PyazLens Logo",
                    modifier = Modifier.size(42.dp).scale(4f)
                )
            }

            Text(
                text = "PyazLens",
                color = PyazPurple,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Heading
        Text(
            text = strings.chooseLanguage,
            color = PyazPurple,
            fontSize = 32.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = strings.selectLanguageSubtitle,
            color = Color(0xFF6F6370),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // English Option Card
        LanguageOptionCard(
            language = "English",
            subtitle = strings.defaultLanguageSub,
            code = "EN",
            flagEmoji = "🇬🇧",
            selected = selectedLanguageCode == LanguageManager.LANG_ENGLISH,
            onClick = { selectedLanguageCode = LanguageManager.LANG_ENGLISH }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Hindi Option Card
        LanguageOptionCard(
            language = "हिंदी",
            subtitle = strings.hindiLanguageSub,
            code = "हि",
            flagEmoji = "🇮🇳",
            selected = selectedLanguageCode == LanguageManager.LANG_HINDI,
            onClick = { selectedLanguageCode = LanguageManager.LANG_HINDI }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Continue Button
        Button(
            onClick = { onContinue(selectedLanguageCode) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PyazPurple)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = strings.continueBtn,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun LanguageOptionCard(
    language: String,
    subtitle: String,
    code: String,
    flagEmoji: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) PyazPurple else PyazCardBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (selected) Color(0xFFF5EFF6) else Color(0xFFF7F4F8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = flagEmoji,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = language,
                    color = PyazPurple,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    color = Color(0xFF7A6F7C),
                    fontSize = 12.sp,
                    lineHeight = 15.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (selected) PyazPurple else Color.Transparent)
                    .border(
                        width = if (selected) 0.dp else 1.5.dp,
                        color = PyazCardBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
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
