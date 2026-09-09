package com.example.pyazlens.ui.settings

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pyazlens.data.network.RetrofitClient
import com.example.pyazlens.ui.theme.PyazLensTheme
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

private val Purple = Color(0xFF511D50)
private val Background = Color(0xFFFCFAFD)
private val Green = Color(0xFF73C943)
private val Gray = Color(0xFF8D8790)
private val BorderGray = Color(0xFFEAE6EB)

@Composable
fun SettingsScreen(
    userName: String,
    userPhone: String,
    userAddress: String,
    userProfileId: Long,
    onProfileUpdated: (
        String,
        String,
        String
    ) -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var notificationsEnabled by remember {
        mutableStateOf(true)
    }

    var selectedLanguage by remember {
        mutableStateOf("English")
    }

    var showProfileDialog by remember {
        mutableStateOf(false)
    }

    var showGuideDialog by remember {
        mutableStateOf(false)
    }

    var showAiDialog by remember {
        mutableStateOf(false)
    }

    var showPrivacyDialog by remember {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 16.dp),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        // --------------------------------------------------
        // HEADER
        // --------------------------------------------------

        item {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Settings",
                color = Purple,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // --------------------------------------------------
        // PROFILE
        // --------------------------------------------------

        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(
                        1.dp,
                        BorderGray,
                        RoundedCornerShape(20.dp)
                    )
                    .padding(18.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Purple.copy(alpha = 0.1f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Person,
                            contentDescription =
                                "Profile",
                            tint = Purple,
                            modifier =
                                Modifier.size(28.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(14.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = userName.ifBlank {
                                "User"
                            },
                            color = Purple,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (userPhone.isNotBlank()) {

                            Text(
                                text = userPhone,
                                color = Gray,
                                fontSize = 13.sp
                            )

                            Text(
                                text = "Phone verified",
                                color = Green,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )

                        } else {

                            Text(
                                text = "Phone number not added",
                                color = Gray,
                                fontSize = 13.sp
                            )
                        }

                        if (userAddress.isNotBlank()) {

                            Text(
                                text = userAddress,
                                color = Gray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF4ECE8))
                            .clickable {
                                showProfileDialog = true
                            }
                            .padding(8.dp)
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Edit,
                            contentDescription =
                                "Edit Profile",
                            tint = Purple,
                            modifier =
                                Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // --------------------------------------------------
        // PREFERENCES
        // --------------------------------------------------

        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(
                        1.dp,
                        BorderGray,
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {

                    Text(
                        text = "Preferences",
                        color = Purple,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Language,
                                contentDescription =
                                    "Language",
                                tint = Purple,
                                modifier =
                                    Modifier.size(22.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(12.dp)
                            )

                            Text(
                                text = "App Language",
                                color = Purple,
                                fontSize = 15.sp,
                                fontWeight =
                                    FontWeight.Medium
                            )
                        }

                        Text(
                            text = selectedLanguage,
                            color = Green,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {

                                selectedLanguage =
                                    if (
                                        selectedLanguage ==
                                        "English"
                                    ) {
                                        "Hindi"
                                    } else {
                                        "English"
                                    }
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Notifications,
                                contentDescription =
                                    "Notifications",
                                tint = Purple,
                                modifier =
                                    Modifier.size(22.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(12.dp)
                            )

                            Text(
                                text =
                                    "Quality Report Notifications",
                                color = Purple,
                                fontSize = 15.sp,
                                fontWeight =
                                    FontWeight.Medium
                            )
                        }

                        Switch(
                            checked =
                                notificationsEnabled,
                            onCheckedChange = {
                                notificationsEnabled = it
                            },
                            colors =
                                SwitchDefaults.colors(
                                    checkedThumbColor =
                                        Color.White,
                                    checkedTrackColor =
                                        Purple
                                )
                        )
                    }
                }
            }
        }

        // --------------------------------------------------
        // AI & HELP
        // --------------------------------------------------

        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(
                        1.dp,
                        BorderGray,
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {

                    Text(
                        text = "AI Model & Help",
                        color = Purple,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    SettingsRowItem(
                        icon = Icons.Default.Memory,
                        label = "AI Engine Information",
                        subLabel =
                            "PyazLens AI • Onion Quality Detection",
                        onClick = {
                            showAiDialog = true
                        }
                    )

                    SettingsRowItem(
                        icon =
                            Icons.Default.HelpOutline,
                        label =
                            "Onion Inspection Guide",
                        subLabel =
                            "Tips for better AI scans",
                        onClick = {
                            showGuideDialog = true
                        }
                    )

                    SettingsRowItem(
                        icon =
                            Icons.Default.PrivacyTip,
                        label =
                            "Privacy & Data Security",
                        subLabel =
                            "Your inspection data",
                        onClick = {
                            showPrivacyDialog = true
                        }
                    )
                }
            }
        }

        // --------------------------------------------------
        // FOOTER
        // --------------------------------------------------

        item {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "PyazLens v1.0 • SIH26031",
                    color = Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text =
                        "AI-Powered Onion Quality Assessment App",
                    color = Gray,
                    fontSize = 11.sp
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }
    }

    // ======================================================
    // EDIT PROFILE
    // ======================================================

    if (showProfileDialog) {

        var editedName by remember {
            mutableStateOf(userName)
        }

        var editedPhone by remember {
            mutableStateOf(userPhone)
        }

        var editedAddress by remember {
            mutableStateOf(userAddress)
        }

        AlertDialog(

            onDismissRequest = {
                showProfileDialog = false
            },

            title = {
                Text("Edit User Profile")
            },

            text = {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    OutlinedTextField(
                        value = editedName,
                        onValueChange = {
                            editedName = it
                        },
                        label = {
                            Text("Full Name")
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editedPhone,
                        onValueChange = {
                            editedPhone = it
                        },
                        label = {
                            Text("Phone Number")
                        },
                        singleLine = true,
                        enabled = userPhone.isBlank()
                    )

                    if (userPhone.isNotBlank()) {

                        Text(
                            text =
                                "Verified phone numbers cannot be changed here.",
                            color = Gray,
                            fontSize = 11.sp
                        )
                    }

                    OutlinedTextField(
                        value = editedAddress,
                        onValueChange = {
                            editedAddress = it
                        },
                        label = {
                            Text("Location / District")
                        },
                        singleLine = true
                    )
                }
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        if (editedName.isBlank()) {

                            Toast.makeText(
                                context,
                                "Name is required",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@TextButton
                        }

                        scope.launch {

                            try {

                                val nameRequest =
                                    editedName
                                        .trim()
                                        .toRequestBody(
                                            "text/plain".toMediaType()
                                        )

                                val phoneRequest =
                                    editedPhone
                                        .trim()
                                        .takeIf {
                                            it.isNotBlank()
                                        }
                                        ?.toRequestBody(
                                            "text/plain".toMediaType()
                                        )

                                val addressRequest =
                                    editedAddress
                                        .trim()
                                        .takeIf {
                                            it.isNotBlank()
                                        }
                                        ?.toRequestBody(
                                            "text/plain".toMediaType()
                                        )

                                val response =
                                    RetrofitClient.api
                                        .updateUserProfile(
                                            userProfileId,
                                            nameRequest,
                                            phoneRequest,
                                            addressRequest
                                        )

                                if (response.success) {

                                    onProfileUpdated(
                                        response.name,
                                        response.phone ?: "",
                                        response.address ?: ""
                                    )

                                    showProfileDialog = false

                                    Toast.makeText(
                                        context,
                                        "Profile updated",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else {

                                    Toast.makeText(
                                        context,
                                        "Unable to update profile",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } catch (e: Exception) {

                                Toast.makeText(
                                    context,
                                    "Update failed: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                ) {

                    Text(
                        text = "Save",
                        color = Purple,
                        fontWeight = FontWeight.Bold
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showProfileDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // ======================================================
    // GUIDE
    // ======================================================

    if (showGuideDialog) {

        AlertDialog(

            onDismissRequest = {
                showGuideDialog = false
            },

            title = {
                Text("🧅 How to Scan Onions")
            },

            text = {

                Text(
                    "1. Place onions on a flat surface in good lighting.\n\n" +
                            "2. Hold your phone above the onions.\n\n" +
                            "3. Make sure all onions are clearly visible.\n\n" +
                            "4. Capture the image or upload one from your gallery.\n\n" +
                            "5. PyazLens will analyze onion size, defects and quality grade."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showGuideDialog = false
                    }
                ) {

                    Text(
                        text = "Got it!",
                        color = Purple,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }

    // ======================================================
    // AI INFO
    // ======================================================

    if (showAiDialog) {

        AlertDialog(

            onDismissRequest = {
                showAiDialog = false
            },

            title = {
                Text("PyazLens AI Engine")
            },

            text = {

                Text(
                    "PyazLens uses computer vision and AI models " +
                            "to detect onions, estimate their size, " +
                            "identify visible defects and assign " +
                            "a quality grade.\n\n" +
                            "The analysis uses the PyazLens backend " +
                            "and trained onion-quality models."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showAiDialog = false
                    }
                ) {

                    Text(
                        text = "OK",
                        color = Purple,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }

    // ======================================================
    // PRIVACY
    // ======================================================

    if (showPrivacyDialog) {

        AlertDialog(

            onDismissRequest = {
                showPrivacyDialog = false
            },

            title = {
                Text("Privacy & Data Security")
            },

            text = {

                Text(
                    "Your inspection records are associated with " +
                            "your PyazLens user profile.\n\n" +
                            "Inspection images and analysis results " +
                            "are stored by the PyazLens backend for " +
                            "your inspection history."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showPrivacyDialog = false
                    }
                ) {

                    Text(
                        text = "OK",
                        color = Purple,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    label: String,
    subLabel: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Purple,
                modifier = Modifier.size(22.dp)
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column {

                Text(
                    text = label,
                    color = Purple,
                    fontSize = 15.sp,
                    fontWeight =
                        FontWeight.Medium
                )

                Text(
                    text = subLabel,
                    color = Gray,
                    fontSize = 12.sp
                )
            }
        }

        Icon(
            imageVector =
                Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {

    PyazLensTheme {

        SettingsScreen(
            userName = "Dev",
            userPhone = "9876543210",
            userAddress = "Hapur",
            userProfileId = 5L,
            onProfileUpdated = { _, _, _ -> }
        )
    }
}