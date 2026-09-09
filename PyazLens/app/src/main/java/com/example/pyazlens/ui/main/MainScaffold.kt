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
import com.example.pyazlens.ui.result.InspectionResultScreen
import com.example.pyazlens.ui.scan.ScanScreen
import com.example.pyazlens.ui.settings.SettingsScreen
import com.example.pyazlens.ui.stats.StatsScreen

import kotlinx.coroutines.launch


@Composable
fun MainScaffold(
    userName: String,
    userPhone: String,
    userAddress: String,
    userProfileId: Long
) {

    // =====================================================
    // CURRENT USER STATE
    // =====================================================

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

                onItemClick = { route ->

                    // -----------------------------------------
                    // HOME
                    // -----------------------------------------

                    if (route == Screen.Home.route) {

                        uploadedImageUri = null
                        analysisResult = null
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

                    onInspectClick = {

                        uploadedImageUri = null
                        analysisResult = null

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

                    onAnalysisComplete = { result ->

                        analysisResult =
                            result

                        mainNavController.navigate(
                            Screen.InspectionResult.route
                        )
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
                        result = result
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

                    onRecordClick = { inspectionId ->

                        scope.launch {

                            try {

                                val response =
                                    RetrofitClient.api
                                        .getInspectionDetails(
                                            inspectionId
                                        )

                                if (response.success) {

                                    analysisResult =
                                        response.inspection
                                            .toAnalyzeResponse()

                                    mainNavController.navigate(
                                        Screen.InspectionResult.route
                                    )

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
                    userProfileId
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

                    id =
                        onion.id,

                    size =
                        OnionSize(

                            diameter_mm =
                                onion.diameter_mm
                                    ?: 0.0,

                            width_mm =
                                onion.width_mm
                                    ?: 0.0,

                            height_mm =
                                onion.height_mm
                                    ?: 0.0
                        ),

                    classification =

                        if (onion.defects.isEmpty())

                            "No Defect"

                        else

                            onion.defects
                                .joinToString(", ") {
                                    it.defect_class
                                },

                    defects =
                        onion.defects.map { defect ->

                            Defect(

                                name =
                                    defect.defect_class,

                                confidence =
                                    defect.confidence
                            )
                        },

                    probabilities =
                        Probabilities(

                            Rotten =
                                probabilityFor(
                                    onion,
                                    "Rotten"
                                ),

                            Sprouted =
                                probabilityFor(
                                    onion,
                                    "Sprouted"
                                ),

                            cutCrack =
                                probabilityFor(
                                    onion,
                                    "Cut/Crack"
                                ),

                            skinDamage =
                                probabilityFor(
                                    onion,
                                    "Skin Damage"
                                ),

                            Sunburned =
                                probabilityFor(
                                    onion,
                                    "Sunburned"
                                ),

                            Misshapen =
                                probabilityFor(
                                    onion,
                                    "Misshapen"
                                )
                        ),

                    grade =
                        onion.grade
                            ?: "Unknown",

                    grade_reason =
                        onion.grade_reason
                            ?: ""
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