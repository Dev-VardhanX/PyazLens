package com.example.pyazlens.ui.userdetails

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.pyazlens.data.language.AppStrings
import com.example.pyazlens.ui.theme.PyazBackground
import com.example.pyazlens.ui.theme.PyazCardBorder
import com.example.pyazlens.ui.theme.PyazGray
import com.example.pyazlens.ui.theme.PyazLensTheme
import com.example.pyazlens.ui.theme.PyazPurple

@Composable
fun UserDetailsScreen(
    initialName: String = "",
    initialPhone: String = "",
    initialLocation: String = "",
    currentLanguage: String = "en",
    onBack: () -> Unit,
    onContinue: (
        fullName: String,
        phone: String,
        location: String
    ) -> Unit
) {
    val strings = AppStrings.getStrings(currentLanguage)

    var fullName by remember(initialName) { mutableStateOf(initialName) }
    var phone by remember(initialPhone) { mutableStateOf(initialPhone) }
    var location by remember(initialLocation) { mutableStateOf(initialLocation) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PyazBackground)
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        // Back Button
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, PyazCardBorder, CircleShape)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = PyazPurple,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Heading
        Text(
            text = strings.enterDetailsTitle,
            color = PyazPurple,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = strings.enterDetailsSub,
            color = Color(0xFF5F5662),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Full Name
        FieldLabel(
            title = strings.fullNameLabel,
            required = true
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = { Text("e.g. Dev Vardhan", color = PyazGray, fontSize = 14.sp) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = PyazPurple, modifier = Modifier.size(22.dp))
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PyazPurple,
                unfocusedBorderColor = PyazCardBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Phone
        FieldLabel(
            title = strings.phoneLabel,
            optional = true,
            optionalTagText = strings.optionalTag
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            placeholder = { Text("+91 00000 00000", color = PyazGray, fontSize = 14.sp) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = PyazPurple, modifier = Modifier.size(22.dp))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PyazPurple,
                unfocusedBorderColor = PyazCardBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Location
        FieldLabel(
            title = strings.locationLabel,
            optional = true,
            optionalTagText = strings.optionalTag
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            placeholder = { Text("e.g. Pilkhuwa, Hapur", color = PyazGray, fontSize = 14.sp) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = PyazPurple, modifier = Modifier.size(22.dp))
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PyazPurple,
                unfocusedBorderColor = PyazCardBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.weight(1f))

        // Main Action Button
        val isPhoneEntered = phone.trim().isNotBlank()

        Button(
            onClick = {
                if (fullName.isNotBlank()) {
                    onContinue(fullName, phone, location)
                }
            },
            enabled = fullName.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PyazPurple,
                disabledContainerColor = PyazPurple.copy(alpha = 0.5f)
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isPhoneEntered) strings.sendOtpBtn else strings.continueBtn,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (isPhoneEntered) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = {
                    if (fullName.isNotBlank()) {
                        onContinue(fullName, "", location)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = strings.continueAnonymousBtn,
                    color = PyazPurple,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun FieldLabel(
    title: String,
    required: Boolean = false,
    optional: Boolean = false,
    optionalTagText: String = ""
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            color = PyazPurple,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        if (required) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "*",
                color = Color(0xFFD94A4A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (optional) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = optionalTagText,
                color = PyazGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserDetailsScreenPreview() {
    PyazLensTheme {
        UserDetailsScreen(
            onBack = {},
            onContinue = { _, _, _ -> }
        )
    }
}