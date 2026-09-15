package com.example.pyazlens.ui.main

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

import com.example.pyazlens.data.network.AnalyzeResponse
import com.example.pyazlens.data.network.Defect
import com.example.pyazlens.data.network.DefectSummary
import com.example.pyazlens.data.network.InspectionDetails
import com.example.pyazlens.data.network.InspectionOnion
import com.example.pyazlens.data.network.Measurement
import com.example.pyazlens.data.network.OnionResult
import com.example.pyazlens.data.network.OnionSize
import com.example.pyazlens.data.network.Probabilities
import com.example.pyazlens.data.network.RetrofitClient
import com.example.pyazlens.data.network.Summary

import com.example.pyazlens.navigation.Screen

import com.example.pyazlens.ui.components.BottomNavigationBar
import com.example.pyazlens.ui.history.HistoryScreen
import com.example.pyazlens.ui.home.HomeScreen
import com.example.pyazlens.data.profile.ProfileManager
import com.example.pyazlens.ui.result.InspectionResultScreen
import com.example.pyazlens.ui.scan.ScanScreen
import com.example.pyazlens.ui.settings.SettingsScreen
import com.example.pyazlens.ui.stats.StatsScreen
import com.example.pyazlens.ui.userdetails.UserDetailsScreen

import kotlinx.coroutines.launch
import android.util.Log

@Composable
fun MainScaffold(
    userName: String,
    userPhone: String,
    userAddress: String,
    userProfileId: Long,
    currentLanguage: String = "en",
    onLanguageChanged: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {

    // =====================================================
    // CURRENT USER STATE
    // =====================================================
    var resultImageUrl by remember { mutableStateOf<String?>(null) }

    var currentUserName by remember {
        mutableStateOf(userName)
    }

    var currentUserPhone by remember {
        mutableStateOf(userPhone)
    }

    var currentUserAddress by remember {
        mutableStateOf(userAddress)
    }


    // =====================================================
    // NAVIGATION
    // =====================================================

    val mainNavController =
        rememberNavController()

    val context =
        LocalContext.current

    val scope =
        rememberCoroutineScope()

    val navBackStackEntry by
    mainNavController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry
            ?.destination
            ?.route
            ?: Screen.Home.route


    // =====================================================
    // IMAGE + RESULT STATE
    // =====================================================

    var uploadedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var resultImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var analysisResult by remember {
        mutableStateOf<AnalyzeResponse?>(null)
    }


    // =====================================================
    // GALLERY
    // =====================================================

    val galleryLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                uploadedImageUri = uri
                analysisResult = null

                mainNavController.navigate(
                    Screen.Scan.route
                )
            }
        }


    // =====================================================
    // MAIN SCAFFOLD
    // =====================================================

    Scaffold(

        bottomBar = {

            BottomNavigationBar(

                currentRoute = currentRoute,
                currentLanguage = currentLanguage,

                onItemClick = { route ->

                    // -----------------------------------------
                    // HOME
                    // -----------------------------------------

                    if (route == Screen.Home.route || route == Screen.Scan.route) {

                        uploadedImageUri = null
                        resultImageUri = null
                        analysisResult = null
                        resultImageUrl = null
                    }

                    mainNavController.navigate(route) {

                        popUpTo(
                            Screen.Home.route
                        ) {
                            saveState = false
                        }

                        launchSingleTop = true

                        restoreState = false
                    }
                }
            )
        }

    ) { paddingValues ->


        NavHost(

            navController =
                mainNavController,

            startDestination =
                Screen.Home.route,

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

        ) {


            // =================================================
            // HOME
            // =================================================

            composable(
                Screen.Home.route
            ) {

                HomeScreen(

                    userName =
                        currentUserName,

                    userProfileId =
                        userProfileId,

                    currentLanguage =
                        currentLanguage,

                    onInspectClick = {

                        uploadedImageUri = null
                        resultImageUri = null
                        analysisResult = null
                        resultImageUrl = null

                        mainNavController.navigate(
                            Screen.Scan.route
                        )
                    },

                    onUploadClick = {

                        galleryLauncher.launch(
                            "image/*"
                        )
                    },

                    onSeeAllClick = {

                        mainNavController.navigate(
                            Screen.History.route
                        )
                    },

                    onInspectionClick = { inspectionId ->

                        scope.launch {

                            try {

                                val response =
                                    RetrofitClient.api
                                        .getInspectionDetails(
                                            inspectionId
                                        )

                                if (response.success) {
                                    analysisResult = response.inspection.toAnalyzeResponse()
                                    resultImageUri = null
                                    resultImageUrl = response.inspection.image_url
                                    mainNavController.navigate(Screen.InspectionResult.route)
                                } else {

                                    Toast.makeText(
                                        context,
                                        "Unable to load inspection",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

//                            } catch (e: Exception) {
//
//                                Toast.makeText(
//                                    context,
//                                    "Failed to load inspection: ${e.message}",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            }
                            } catch (e: Exception) {

                                    Log.e(
                                        "INSPECTION_ERROR",
                                        "Failed to load inspection",
                                        e
                                    )

                                    Toast.makeText(
                                        context,
                                        "ERROR: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                    },

                    onProfileClick = {

                        mainNavController.navigate(
                            Screen.UserDetails.route
                        )
                    }
                )
            }


            // =================================================
            // SCAN
            // =================================================

            composable(
                Screen.Scan.route
            ) {

                ScanScreen(

                    initialImageUri =
                        uploadedImageUri,

                    userName =
                        currentUserName,

                    userPhone =
                        currentUserPhone,

                    userAddress =
                        currentUserAddress,

                    userProfileId =
                        userProfileId,

                    currentLanguage =
                        currentLanguage,

                    onAnalysisComplete = { result, imageUri ->
                        resultImageUri = imageUri
                        resultImageUrl = null
                        analysisResult = result
                        mainNavController.navigate(Screen.InspectionResult.route)
                    }
                )
            }


            // =================================================
            // INSPECTION RESULT
            // =================================================

            composable(
                Screen.InspectionResult.route
            ) {

                analysisResult?.let { result ->
                    InspectionResultScreen(
                        result = result,
                        imageUri = resultImageUri,
                        imageUrl = resultImageUrl,
                        currentLanguage = currentLanguage,
                        onDone = {
                            uploadedImageUri = null
                            resultImageUri = null
                            analysisResult = null

                            mainNavController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }


            // =================================================
            // HISTORY
            // =================================================

            composable(
                Screen.History.route
            ) {

                HistoryScreen(

                    userProfileId =
                        userProfileId,

                    currentLanguage =
                        currentLanguage,

                    onRecordClick = { inspectionId ->

                        scope.launch {

                            try {

                                val response =
                                    RetrofitClient.api
                                        .getInspectionDetails(
                                            inspectionId
                                        )

                                if (response.success) {
                                    analysisResult = response.inspection.toAnalyzeResponse()
                                    resultImageUri = null
                                    resultImageUrl = response.inspection.image_url
                                    mainNavController.navigate(Screen.InspectionResult.route)
                                } else {

                                    Toast.makeText(
                                        context,
                                        "Unable to load inspection",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } catch (e: Exception) {

                                Toast.makeText(
                                    context,
                                    "Failed to load inspection: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },

                    onDeleteRecord = { inspectionId ->

                        scope.launch {

                            try {

                                val response =
                                    RetrofitClient.api
                                        .deleteInspection(
                                            inspectionId
                                        )

                                if (response.success) {

                                    Toast.makeText(
                                        context,
                                        "Inspection deleted successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    mainNavController.navigate(
                                        Screen.History.route
                                    ) {

                                        popUpTo(
                                            Screen.History.route
                                        ) {
                                            inclusive = true
                                        }
                                    }

                                } else {

                                    Toast.makeText(
                                        context,
                                        "Unable to delete inspection",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } catch (e: Exception) {

                                Toast.makeText(
                                    context,
                                    "Delete failed: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

            })
        }


        // =================================================
        // STATS / INSIGHTS
        // =================================================

        composable(
            Screen.Insights.route
        ) {

            StatsScreen(
                userProfileId =
                    userProfileId,

                currentLanguage =
                    currentLanguage
            )
        }


        // =================================================
        // SETTINGS
        // =================================================

        composable(
            Screen.Settings.route
        ) {

            SettingsScreen(

                userName =
                    currentUserName,

                userPhone =
                    currentUserPhone,

                userAddress =
                    currentUserAddress,

                userProfileId =
                    userProfileId,

                currentLanguage =
                    currentLanguage,

                onLanguageChanged =
                    onLanguageChanged,

                onProfileUpdated = {
                        updatedName,
                        updatedPhone,
                        updatedAddress ->

                    currentUserName =
                        updatedName

                    currentUserPhone =
                        updatedPhone

                    currentUserAddress =
                        updatedAddress

                    ProfileManager.saveProfile(
                        context = context,
                        profileId = userProfileId,
                        name = updatedName,
                        phone = updatedPhone,
                        address = updatedAddress
                    )
                },
                onLogout = onLogout,
                onNavigateToProfile = {
                    mainNavController.navigate(Screen.UserDetails.route)
                }
            )
        }

        // =================================================
        // USER DETAILS / PROFILE
        // =================================================

        composable(
            Screen.UserDetails.route
        ) {
            UserDetailsScreen(
                initialName = currentUserName,
                initialPhone = currentUserPhone,
                initialLocation = currentUserAddress,
                currentLanguage = currentLanguage,
                onBack = {
                    mainNavController.popBackStack()
                },
                onContinue = { fullName, phone, location ->
                    currentUserName = fullName.trim()
                    currentUserPhone = phone.trim()
                    currentUserAddress = location.trim()

                    ProfileManager.saveProfile(
                        context = context,
                        profileId = userProfileId,
                        name = currentUserName,
                        phone = currentUserPhone,
                        address = currentUserAddress
                    )

                    scope.launch {
                        try {
                            val nameReq = currentUserName.toRequestBody("text/plain".toMediaType())
                            val phoneReq = currentUserPhone.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaType())
                            val addressReq = currentUserAddress.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaType())

                            RetrofitClient.api.updateUserProfile(
                                userProfileId = userProfileId,
                                name = nameReq,
                                phone = phoneReq,
                                address = addressReq
                            )
                        } catch (_: Exception) {}
                    }

                    mainNavController.popBackStack()
                }
            )
        }
    }
}
}


// =========================================================
// HISTORY DETAIL → RESULT FORMAT
// =========================================================

private fun InspectionDetails.toAnalyzeResponse():
        AnalyzeResponse {

    return AnalyzeResponse(

        success = true,

        user_profile_id =
            user_profile_id ?: 0L,

        image_url =
            image_url,

        total_onions =
            total_onions,

        measurement =
            Measurement(
                reference_coin_diameter_mm =
                    27.0,

                mm_per_pixel =
                    0.0
            ),

        onions =
            onions.map { onion ->

                OnionResult(
                    id = onion.id,

                    bbox = onion.bbox ?: emptyList(),
                    segmentation = onion.segmentation ?: emptyList(),
                    crop_url = onion.crop_url ?: "",

                    size = OnionSize(
                        diameter_mm = onion.diameter_mm ?: 0.0,
                        width_mm = onion.width_mm ?: 0.0,
                        height_mm = onion.height_mm ?: 0.0
                    ),

                    classification =
                        if (onion.defects.isEmpty())
                            "No Defect"
                        else
                            onion.defects.joinToString(", ") {
                                it.defect_class ?: "Unknown"
                            },

                    defects = onion.defects.map { defect ->
                        Defect(
                            name = defect.defect_class ?: "Unknown",
                            confidence = defect.confidence ?: 0.0
                        )
                    },

                    probabilities = Probabilities(
                        Rotten = probabilityFor(onion, "Rotten"),
                        Sprouted = probabilityFor(onion, "Sprouted"),
                        cutCrack = probabilityFor(onion, "Cut/Crack"),
                        skinDamage = probabilityFor(onion, "Skin Damage"),
                        Sunburned = probabilityFor(onion, "Sunburned"),
                        Misshapen = probabilityFor(onion, "Misshapen")
                    ),

                    grade = onion.grade ?: "Unknown",

                    grade_reason = onion.grade_reason ?: ""
                )
            },

        summary =
            Summary(

                grade_a =
                    grade_a_count
                        ?: 0,

                grade_urs =
                    urs_count
                        ?: 0,

                rejected =
                    rejected_count
                        ?: 0,

                grade_a_percentage =
                    grade_a_percentage
                        ?: 0.0,

                grade_urs_percentage =
                    urs_percentage
                        ?: 0.0,

                rejected_percentage =
                    rejected_percentage
                        ?: 0.0
            ),

        defect_summary =
            DefectSummary(

                Rotten =
                    rotten_count
                        ?: 0,

                cutCrack =
                    cut_crack_count
                        ?: 0,

                Sprouted =
                    sprouted_count
                        ?: 0,

                skinDamage =
                    skin_damage_count
                        ?: 0,

                Sunburned =
                    sunburned_count
                        ?: 0,

                Misshapen =
                    misshapen_count
                        ?: 0
            )
    )
}


// =========================================================
// DEFECT CONFIDENCE
// =========================================================

private fun probabilityFor(
    onion: InspectionOnion,
    defectName: String
): Double {

    return onion.defects
        .firstOrNull {
            it.defect_class == defectName
        }
        ?.confidence
        ?: 0.0
}