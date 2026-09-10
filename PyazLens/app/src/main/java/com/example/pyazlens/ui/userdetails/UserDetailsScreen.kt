package com.example.pyazlens.ui.userdetails

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pyazlens.ui.theme.PyazLensTheme
import androidx.compose.ui.focus.onFocusChanged

private val Purple = Color(0xFF511D50)
private val Background = Color(0xFFFCFAFD)
private val LightGray = Color(0xFFF8F7F9)
private val GrayText = Color(0xFF8F8992)
private val BorderGray = Color(0xFFE9E5EA)

@Composable
fun UserDetailsScreen(
    onBack: () -> Unit,
    onContinue: (
        fullName: String,
        phone: String,
        location: String
    ) -> Unit
) {

    var fullName by remember {
        mutableStateOf("")
    }

    var phone by remember {
        mutableStateOf("")
    }

    var location by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {

        // --------------------------------
        // Back button
        // --------------------------------

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

        Spacer(modifier = Modifier.height(20.dp))

        // Heading

        Text(
            text = "Tell us about yourself",
            color = Purple,
            fontSize = 31.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "This helps us personalize your inspection\nexperience.",
            color = Color(0xFF5F5662),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --------------------------------
        // Full Name
        // --------------------------------

        FieldLabel(
            title = "Full Name",
            required = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        UserInputField(
            value = fullName,
            onValueChange = {
                fullName = it
            },
            placeholder = "e.g. Dev Vardhan",
            icon = { color ->
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --------------------------------
        // Phone
        // --------------------------------

        FieldLabel(
            title = "Phone Number",
            optional = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        UserInputField(
            value = phone,
            onValueChange = {
                phone = it
            },
            placeholder = "+91 00000 00000",
            keyboardType = KeyboardType.Phone,
            icon = { color ->
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --------------------------------
        // Location
        // --------------------------------

        FieldLabel(
            title = "Location / District",
            optional = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        UserInputField(
            value = location,
            onValueChange = {
                location = it
            },
            placeholder = "e.g. Pilkhuwa, Hapur",
            icon = { color ->
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
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
                .clip(RoundedCornerShape(28.dp))
                .background(Purple)
                .clickable {

                    if (fullName.isNotBlank()) {
                        onContinue(
                            fullName,
                            phone,
                            location
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Continue",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(6.dp))

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

@Composable
private fun FieldLabel(
    title: String,
    required: Boolean = false,
    optional: Boolean = false
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(color = Purple)
                ) {
                    append(title)
                }

                if (required) {
                    withStyle(
                        style = SpanStyle(color = Color.Red)
                    ) {
                        append(" *")
                    }
                }
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        if (optional) {
            Text(
                text = "OPTIONAL",
                color = Color(0xFF96909A),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
private fun UserInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: @Composable (Color) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var isFocused by remember {
        mutableStateOf(false)
    }
    val iconColor = if (isFocused) Purple else Color(0xFF9DA4B0)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,

        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },

        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFFC4C7CF),
                fontSize = 16.sp
            )
        },

        leadingIcon = {
            icon(iconColor)
        },

        singleLine = true,

        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),

        shape = RoundedCornerShape(18.dp),

        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,

            focusedBorderColor = Purple,
            unfocusedBorderColor = BorderGray,

            cursorColor = Purple
        ),
        textStyle = LocalTextStyle.current.copy(
            fontSize = 18.sp,
            color = Color(0xFF2D252D)
        ),
    )
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