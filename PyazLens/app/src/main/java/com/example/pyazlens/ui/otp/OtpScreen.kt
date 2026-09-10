package com.example.pyazlens.ui.otp

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pyazlens.ui.theme.PyazLensTheme
import kotlinx.coroutines.delay

private val Purple = Color(0xFF511D50)
private val Background = Color(0xFFFCFAFD)
private val GrayText = Color(0xFF5F5662)
private val BorderGray = Color(0xFFE9E5EA)

@Composable
fun OtpScreen(
    phone: String,
    onBack: () -> Unit,
    onVerify: (String) -> Unit,
    onResend: () -> Unit
) {

    var otp by remember {
        mutableStateOf("")
    }

    var secondsRemaining by remember {
        mutableStateOf(30)
    }

    LaunchedEffect(secondsRemaining) {
        if (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(
                    1.dp,
                    BorderGray,
                    CircleShape
                )
                .clickable {
                    onBack()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF9BA0AA),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Verify your phone",
            color = Purple,
            fontSize = 31.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Enter the 6-digit OTP sent to",
            color = GrayText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = phone,
            color = Purple,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(35.dp))

        Text(
            text = "One-Time Password",
            color = Purple,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        BasicTextField(
            value = otp,
            onValueChange = {
                if (
                    it.length <= 6 &&
                    it.all { character -> character.isDigit() }
                ) {
                    otp = it
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(6) { index ->

                        val character =
                            otp.getOrNull(index)?.toString() ?: ""

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White)
                                .border(
                                    1.dp,
                                    if (index == otp.length)
                                        Purple
                                    else
                                        BorderGray,
                                    RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = character,
                                color = Color(0xFF2D252D),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (secondsRemaining > 0) {
                "Resend OTP in ${secondsRemaining}s"
            } else {
                "Didn't receive the OTP? Resend"
            },
            color = if (secondsRemaining > 0)
                Color(0xFF96909A)
            else
                Purple,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(
                enabled = secondsRemaining == 0
            ) {
                otp = ""
                secondsRemaining = 30
                onResend()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    if (otp.length == 6)
                        Purple
                    else
                        Color(0xFFD8D2D8)
                )
                .clickable(
                    enabled = otp.length == 6
                ) {
                    onVerify(otp)
                },
            contentAlignment = Alignment.Center
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Verify & Continue",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(6.dp))

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun OtpScreenPreview() {
    PyazLensTheme {
        OtpScreen(
            phone = "+91 98765 43210",
            onBack = {},
            onVerify = {},
            onResend = {}
        )
    }
}