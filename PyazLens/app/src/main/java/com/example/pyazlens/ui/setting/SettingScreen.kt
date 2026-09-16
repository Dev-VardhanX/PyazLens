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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room3.vo.Warning
import com.example.pyazlens.data.language.AppStrings
import com.example.pyazlens.data.language.LanguageManager
import com.example.pyazlens.data.network.RetrofitClient
import com.example.pyazlens.ui.theme.PyazLensTheme
import com.example.pyazlens.ui.theme.PyazPurple
import com.example.pyazlens.ui.theme.PyazGreen
import com.example.pyazlens.ui.theme.PyazBackground
import com.example.pyazlens.ui.theme.PyazGray
import com.example.pyazlens.ui.theme.PyazCardBorder
import com.example.pyazlens.ui.theme.PyazReject
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
fun SettingsScreen(
    userName: String,
    userPhone: String,
    userAddress: String,
    userProfileId: Long,
    currentLanguage: String = "en",
    onLanguageChanged: (String) -> Unit = {},
    onProfileUpdated: (String, String, String) -> Unit,
    onLogout: () -> Unit = {},
    onNavigateToProfile: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val strings = AppStrings.getStrings(currentLanguage)

    var showLogoutConfirmation by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showGuideDialog by remember { mutableStateOf(false) }
    var showAiDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PyazBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. TOP TITLE HEADER
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = strings.settingsTitle,
                color = PyazPurple,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // 2. ACCOUNT HERO CARD
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = strings.accountSectionTitle,
                    color = PyazPurple.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, PyazCardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar Badge
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(PyazPurple, Color(0xFF381037))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.trim().take(1).uppercase().ifBlank { "U" },
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userName.ifBlank { "User" },
                                color = PyazPurple,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (userPhone.isNotBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFEAF7E5))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = strings.phoneVerifiedTag,
                                            color = PyazGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                } else {
                                    Text(
                                        text = strings.phoneNotAdded,
                                        color = PyazGray,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            if (userAddress.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = PyazGray,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = userAddress,
                                        color = PyazGray,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Edit Button Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PyazPurple.copy(alpha = 0.08f))
                                .clickable {
                                    if (onNavigateToProfile != null) {
                                        onNavigateToProfile()
                                    } else {
                                        showProfileDialog = true
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = strings.editProfileTitle,
                                    tint = PyazPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = PyazPurple,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. PREFERENCES SECTION
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = strings.preferencesTitle,
                    color = PyazPurple.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, PyazCardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        // Language Row
                        val langDisplay = LanguageManager.getLanguageDisplayName(currentLanguage)
                        SettingsRowItem(
                            icon = Icons.Default.Language,
                            label = strings.appLanguageItem,
                            subLabel = null,
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(PyazGreen.copy(alpha = 0.12f))
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = langDisplay,
                                            color = PyazGreen,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = PyazGray.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            onClick = {
                                val nextLang = if (currentLanguage == LanguageManager.LANG_HINDI) LanguageManager.LANG_ENGLISH else LanguageManager.LANG_HINDI
                                onLanguageChanged(nextLang)
                            }
                        )
                    }
                }
            }
        }

        // 4. INSPECTION & AI SECTION
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = strings.aiHelpSectionTitle,
                    color = PyazPurple.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, PyazCardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        SettingsRowItem(
                            icon = Icons.AutoMirrored.Filled.HelpOutline,
                            label = strings.inspectionGuideItem,
                            subLabel = strings.inspectionGuideSub,
                            iconBackgroundColor = Color(0xFFFFF7EA),
                            iconTint = Color(0xFFD49320),
                            onClick = { showGuideDialog = true }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Color(0xFFF0E6F2)
                        )

                        SettingsRowItem(
                            icon = Icons.Default.Memory,
                            label = strings.aiEngineInfoItem,
                            subLabel = strings.aiEngineSub,
                            iconBackgroundColor = PyazPurple.copy(alpha = 0.08f),
                            iconTint = PyazPurple,
                            onClick = { showAiDialog = true }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Color(0xFFF0E6F2)
                        )

                        SettingsRowItem(
                            icon = Icons.Default.PrivacyTip,
                            label = strings.privacySecurityItem,
                            subLabel = strings.privacySecuritySub,
                            iconBackgroundColor = PyazGreen.copy(alpha = 0.10f),
                            iconTint = PyazGreen,
                            onClick = { showPrivacyDialog = true }
                        )
                    }
                }
            }
        }

        // 5. ABOUT PYAZLENS SECTION
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = strings.aboutSectionTitle,
                    color = PyazPurple.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, PyazCardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PyazGreen.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🧅", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PyazLens",
                                color = PyazPurple,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = strings.appVersionSub,
                                color = PyazGray,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // 6. LOGOUT ACTION CARD
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLogoutConfirmation = true },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7F7)),
                border = BorderStroke(1.dp, Color(0xFFFFE2E2)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PyazReject.copy(alpha = 0.10f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = strings.logoutBtn,
                                tint = PyazReject,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = strings.logoutBtn,
                            color = PyazReject,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = PyazReject.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 7. CLEAN PRODUCT FOOTER
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PyazLens",
                    color = PyazPurple.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (currentLanguage == "hi") "एआई-संचालित प्याज गुणवत्ता जांच" else "AI-powered onion quality inspection",
                    color = PyazGray,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // LOGOUT CONFIRMATION DIALOG
    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = { Text(strings.logoutConfirmTitle) },
            text = { Text(strings.logoutConfirmText) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutConfirmation = false
                    onLogout()
                }) {
                    Text(strings.logoutBtn, color = PyazReject, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
                    Text(strings.cancelBtn)
                }
            }
        )
    }

    // EDIT PROFILE DIALOG
    if (showProfileDialog) {
        var editedName by remember { mutableStateOf(userName) }
        var editedPhone by remember { mutableStateOf(userPhone) }
        var editedAddress by remember { mutableStateOf(userAddress) }

        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text(strings.editProfileTitle) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text(strings.fullNameLabel) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editedPhone,
                        onValueChange = { editedPhone = it },
                        label = { Text(strings.phoneLabel) },
                        singleLine = true,
                        enabled = userPhone.isBlank()
                    )
                    if (userPhone.isNotBlank()) {
                        Text(
                            text = strings.phoneVerifiedTag,
                            color = PyazGray,
                            fontSize = 11.sp
                        )
                    }
                    OutlinedTextField(
                        value = editedAddress,
                        onValueChange = { editedAddress = it },
                        label = { Text(strings.locationLabel) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editedName.isBlank()) {
                            Toast.makeText(context, strings.fullNameLabel, Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                        scope.launch {
                            try {
                                val nameRequest = editedName.trim().toRequestBody("text/plain".toMediaType())
                                val phoneRequest = editedPhone.trim().takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaType())
                                val addressRequest = editedAddress.trim().takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaType())

                                val response = RetrofitClient.api.updateUserProfile(
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
                                    Toast.makeText(context, strings.saveBtn, Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, strings.connectionFailed, Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "${strings.connectionFailed}: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                ) {
                    Text(text = strings.saveBtn, color = PyazPurple, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text(strings.cancelBtn)
                }
            }
        )
    }

    // GUIDE DIALOG
    if (showGuideDialog) {
        OnionInspectionGuideDialog(
            currentLanguage = currentLanguage,
            onDismiss = { showGuideDialog = false }
        )
    }

    // AI INFO DIALOG
//    if (showAiDialog) {
//        AlertDialog(
//            onDismissRequest = { showAiDialog = false },
//            title = { Text(strings.aiDialogTitle) },
//            text = { Text(strings.aiDialogText) },
//            confirmButton = {
//                TextButton(onClick = { showAiDialog = false }) {
//                    Text(text = strings.okBtn, color = PyazPurple, fontWeight = FontWeight.Bold)
//                }
//            }
//        )
//    }
    if (showAiDialog) {
        Dialog(
            onDismissRequest = { showAiDialog = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxWidth(0.88f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PyazPurple)
                            .padding(horizontal = 18.dp, vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Memory,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(23.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = strings.aiDialogTitle,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = strings.aiDialogSubtitle,
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }

                            IconButton(
                                onClick = { showAiDialog = false }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = strings.close,
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {

                        Text(
                            text = strings.aiDialogDescription,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            color = Color(0xFF444444)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        AiInfoCard(
                            icon = Icons.Default.Visibility,
                            title = strings.onionDetectionTitle,
                            description = strings.onionDetectionDescription
                        )

                        Spacer(modifier = Modifier.height(7.dp))

                        AiInfoCard(
                            icon = Icons.Default.Straighten,
                            title = strings.sizeMeasurementTitle,
                            description = strings.sizeMeasurementDescription
                        )

                        Spacer(modifier = Modifier.height(7.dp))

                        AiInfoCard(
                            icon = Icons.Default.Analytics,
                            title = strings.defectQualityTitle,
                            description = strings.defectQualityDescription
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        HorizontalDivider(
                            color = Color(0xFFEAEAEA)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = strings.aiFooterTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PyazPurple
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = strings.aiFooterDescription,
                            fontSize = 12.sp,
                            color = Color(0xFF777777)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showAiDialog = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PyazPurple
                            )
                        ) {
                            Text(
                                text = strings.gotItBtn,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
    // PRIVACY DIALOG
    // PRIVACY DIALOG
    if (showPrivacyDialog) {
        Dialog(
            onDismissRequest = { showPrivacyDialog = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.88f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    // HEADER
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PyazPurple)
                            .padding(horizontal = 18.dp, vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Color.White.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PrivacyTip,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(23.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = strings.privacyDialogTitle,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = strings.privacyDialogDescription,
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }

                            IconButton(
                                onClick = { showPrivacyDialog = false }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = strings.close,
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // CONTENT
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {

                        Text(
                            text = strings.privacyDialogDescription,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            color = Color(0xFF444444)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        PrivacyInfoCard(
                            icon = Icons.Default.Image,
                            title = strings.inspectionImagesTitle,
                            description = strings.inspectionImagesDescription
                        )

                        Spacer(modifier = Modifier.height(7.dp))

                        PrivacyInfoCard(
                            icon = Icons.Default.Analytics,
                            title = strings.analysisResultsTitle,
                            description = strings.analysisResultsDescription
                        )

                        Spacer(modifier = Modifier.height(7.dp))

                        PrivacyInfoCard(
                            icon = Icons.Default.Person,
                            title = strings.yourProfileTitle,
                            description = strings.yourProfileDescription
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        HorizontalDivider(
                            color = Color(0xFFEAEAEA)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = strings.privacyFooterTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PyazPurple
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = strings.privacyFooterDescription,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFF777777)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showPrivacyDialog = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PyazPurple
                            )
                        ) {
                            Text(
                                text = strings.gotItBtn,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    label: String,
    subLabel: String? = null,
    iconBackgroundColor: Color = PyazPurple.copy(alpha = 0.08f),
    iconTint: Color = PyazPurple,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    color = PyazPurple,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                if (!subLabel.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subLabel,
                        color = PyazGray,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = PyazGray.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }
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
            currentLanguage = "en",
            onProfileUpdated = { _, _, _ -> }
        )
    }
}

@Composable
fun OnionInspectionGuideDialog(
    currentLanguage: String = "en",
    onDismiss: () -> Unit
) {
    val strings = AppStrings.getStrings(currentLanguage)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFCFAFD)
            ),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                // ─────────────────────────────────────
                // HEADER
                // ─────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PyazPurple)
                        .padding(
                            start = 20.dp,
                            end = 12.dp,
                            top = 18.dp,
                            bottom = 18.dp
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = strings.guideDialogTitle,
                                color = Color.White,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = strings.guideDialogSubtitle,
                                color = Color(0xFFE8DDEA),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                // ─────────────────────────────────────
                // CONTENT
                // ─────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    GuideCompactStep(
                        number = "1",
                        title = strings.guideStep1Title,
                        description = strings.guideStep1Desc
                    )

                    GuideCompactStep(
                        number = "2",
                        title = strings.guideStep2Title,
                        description = strings.guideStep2Desc,
                        highlighted = true
                    )

                    GuideCompactStep(
                        number = "3",
                        title = strings.guideStep3Title,
                        description = strings.guideStep3Desc
                    )

                    GuideCompactStep(
                        number = "4",
                        title = strings.guideStep4Title,
                        description = strings.guideStep4Desc
                    )

                    GuideCompactStep(
                        number = "5",
                        title = strings.guideStep5Title,
                        description = strings.guideStep5Desc
                    )

                    GuideCompactStep(
                        number = "6",
                        title = strings.guideStep6Title,
                        description = strings.guideStep6Desc
                    )

                    // ─────────────────────────────────
                    // PHOTO GUIDELINES
                    // ─────────────────────────────────

                    Text(
                        text = if (currentLanguage == "hi")
                            "अच्छी फोटो कैसे लें"
                        else
                            "Get the Best Inspection Photo",
                        color = PyazPurple,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            top = 6.dp,
                            bottom = 2.dp
                        )
                    )

                    // GOOD PHOTO
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFEAF7E5)
                        ),
                        border = BorderStroke(
                            1.dp,
                            Color(0xFFD3EACB)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF55A83A),
                                    modifier = Modifier.size(22.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = strings.guideGoodPhotoTitle,
                                    color = Color(0xFF2E6B1D),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            GuidePointRow(
                                text = strings.guideGoodPhotoPoint1,
                                isGood = true
                            )
                            GuidePointRow(
                                text = strings.guideGoodPhotoPoint2,
                                isGood = true
                            )
                            GuidePointRow(
                                text = strings.guideGoodPhotoPoint3,
                                isGood = true
                            )
                            GuidePointRow(
                                text = strings.guideGoodPhotoPoint4,
                                isGood = true
                            )
                            GuidePointRow(
                                text = strings.guideGoodPhotoPoint5,
                                isGood = true
                            )
                        }
                    }

                    // AVOID
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF4F4)
                        ),
                        border = BorderStroke(
                            1.dp,
                            Color(0xFFF0D1D1)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFD94A4A),
                                    modifier = Modifier.size(22.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = strings.guideBadPhotoTitle,
                                    color = Color(0xFF9E1F1F),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            GuidePointRow(
                                text = strings.guideBadPhotoPoint1,
                                isGood = false
                            )
                            GuidePointRow(
                                text = strings.guideBadPhotoPoint2,
                                isGood = false
                            )
                            GuidePointRow(
                                text = strings.guideBadPhotoPoint3,
                                isGood = false
                            )
                            GuidePointRow(
                                text = strings.guideBadPhotoPoint4,
                                isGood = false
                            )
                            GuidePointRow(
                                text = strings.guideBadPhotoPoint5,
                                isGood = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }

                // ─────────────────────────────────────
                // BOTTOM BUTTON
                // ─────────────────────────────────────
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 18.dp,
                                vertical = 14.dp
                            )
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PyazPurple
                        )
                    ) {
                        Text(
                            text = strings.gotItBtn,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GuideCompactStep(
    number: String,
    title: String,
    description: String,
    highlighted: Boolean = false
) {
    val backgroundColor =
        if (highlighted) Color(0xFFFFF8E8)
        else Color.White

    val borderColor =
        if (highlighted) Color(0xFFE7C96A)
        else PyazCardBorder

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (highlighted) 1.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        if (highlighted)
                            Color(0xFFE9B82E)
                        else
                            PyazPurple
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = PyazPurple,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    color = PyazGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun GuideStepCard(
    stepNumber: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F7FA)),
        border = BorderStroke(1.dp, PyazCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(PyazPurple),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = PyazPurple,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = Color(0xFF4A444D),
                    fontSize = 13.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun GuideCoinStepCard(
    stepNumber: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9EC)),
        border = BorderStroke(1.5.dp, Color(0xFFE6A117))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE6A117)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = Color(0xFF8A5A00),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "🪙 ₹10",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFB57400)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                color = Color(0xFF59410E),
                fontSize = 13.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun GuideGradingStepCard(
    stepNumber: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F7FA)),
        border = BorderStroke(1.dp, PyazCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(PyazPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = PyazPurple,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                GradeChip(label = "GRADE A", color = Color(0xFF55A83A), bgColor = Color(0xFFEAF7E5))
                GradeChip(label = "URS", color = Color(0xFFD49320), bgColor = Color(0xFFFFF4D9))
                GradeChip(label = "REJECT", color = Color(0xFFD94A4A), bgColor = Color(0xFFFFE3E3))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                color = Color(0xFF4A444D),
                fontSize = 13.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun GradeChip(label: String, color: Color, bgColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun GuidePointRow(text: String, isGood: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "• ",
            color = if (isGood) Color(0xFF55A83A) else Color(0xFFD94A4A),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            color = if (isGood) Color(0xFF2E6B1D) else Color(0xFF9E1F1F),
            fontSize = 13.sp,
            lineHeight = 16.sp
        )
    }
}
@Composable
private fun AiInfoCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF7F5F9))
            .padding(11.dp),
        verticalAlignment = Alignment.Top
    ) {

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PyazPurple.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PyazPurple,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun PrivacyInfoCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF7F5F9))
            .padding(11.dp),
        verticalAlignment = Alignment.Top
    ) {

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PyazPurple.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PyazPurple,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color(0xFF666666)
            )
        }
    }
}