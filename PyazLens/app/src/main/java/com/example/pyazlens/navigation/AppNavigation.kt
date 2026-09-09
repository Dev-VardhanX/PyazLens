package com.example.pyazlens.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pyazlens.data.network.RetrofitClient
import com.example.pyazlens.ui.language.LanguageScreen
import com.example.pyazlens.ui.main.MainScaffold
import com.example.pyazlens.ui.otp.OtpScreen
import com.example.pyazlens.ui.splash.SplashScreen
import com.example.pyazlens.ui.userdetails.UserDetailsScreen
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var userName by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var userAddress by remember { mutableStateOf("") }

    var userProfileId by remember {
        mutableStateOf<Long?>(null)
    }

    var devOtp by remember {
        mutableStateOf<String?>(null)
    }

    var verificationId by remember {
        mutableStateOf<String?>(null)
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // --------------------------------------------------
        // SPLASH
        // --------------------------------------------------

        composable(Screen.Splash.route) {

            SplashScreen(
                onSplashFinished = {

                    navController.navigate(Screen.Language.route) {

                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // --------------------------------------------------
        // LANGUAGE
        // --------------------------------------------------

        composable(Screen.Language.route) {

            LanguageScreen(
                onContinue = {

                    navController.navigate(
                        Screen.UserDetails.route
                    )
                }
            )
        }

        // --------------------------------------------------
        // USER DETAILS
        // --------------------------------------------------

        composable(Screen.UserDetails.route) {

            UserDetailsScreen(

                onBack = {
                    navController.popBackStack()
                },

                onContinue = { fullName, phone, location ->

                    userName = fullName
                    userPhone = phone
                    userAddress = location

                    // ------------------------------------------
                    // NO PHONE
                    // ------------------------------------------

                    if (phone.isBlank()) {

                        scope.launch {

                            try {

                                val nameRequest =
                                    fullName
                                        .trim()
                                        .toRequestBody(
                                            "text/plain".toMediaType()
                                        )

                                val addressRequest =
                                    location
                                        .trim()
                                        .takeIf {
                                            it.isNotBlank()
                                        }
                                        ?.toRequestBody(
                                            "text/plain".toMediaType()
                                        )

                                val response =
                                    RetrofitClient.api.createOrGetUser(

                                        name = nameRequest,

                                        phone = null,

                                        address = addressRequest
                                    )

                                if (response.success) {

                                    userProfileId =
                                        response.user_profile_id

                                    navController.navigate(
                                        Screen.Home.route
                                    ) {

                                        popUpTo(
                                            Screen.Language.route
                                        ) {
                                            inclusive = true
                                        }
                                    }

                                } else {

                                    Toast.makeText(
                                        context,
                                        "Unable to create user profile",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } catch (e: Exception) {

                                Toast.makeText(
                                    context,
                                    "Connection error: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    } else {

                        // ------------------------------------------
                        // PHONE PROVIDED → SEND OTP
                        // ------------------------------------------

                        scope.launch {

                            try {

                                val response =
                                    RetrofitClient.api.sendOtp(
                                        phone.trim()
                                    )

                                if (response.success) {

                                    devOtp =
                                        response.dev_otp

                                    verificationId =
                                        response.verification_id

                                    navController.navigate(
                                        Screen.Otp.route
                                    )

                                } else {

                                    Toast.makeText(
                                        context,
                                        response.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } catch (e: Exception) {

                                Toast.makeText(
                                    context,
                                    "Unable to send OTP: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            )
        }

        // --------------------------------------------------
        // OTP
        // --------------------------------------------------

        composable(Screen.Otp.route) {

            OtpScreen(

                phone = userPhone,

                devOtp = devOtp,

                onBack = {
                    navController.popBackStack()
                },

                onVerify = { otp ->

                    scope.launch {

                        try {

                            val response =
                                RetrofitClient.api.verifyOtp(
                                    phone = userPhone.trim(),
                                    otp = otp
                                )

                            if (!response.success ||
                                !response.verified
                            ) {

                                Toast.makeText(
                                    context,
                                    response.message,
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@launch
                            }

                            val returnedVerificationId =
                                response.verification_id

                            if (
                                returnedVerificationId.isNullOrBlank()
                            ) {

                                Toast.makeText(
                                    context,
                                    "Verification session expired. Please try again.",
                                    Toast.LENGTH_LONG
                                ).show()

                                return@launch
                            }

                            verificationId =
                                returnedVerificationId

                            // --------------------------------------
                            // NOW CREATE / GET VERIFIED PROFILE
                            // --------------------------------------

                            val nameRequest =
                                userName
                                    .trim()
                                    .toRequestBody(
                                        "text/plain".toMediaType()
                                    )

                            val phoneRequest =
                                userPhone
                                    .trim()
                                    .toRequestBody(
                                        "text/plain".toMediaType()
                                    )

                            val addressRequest =
                                userAddress
                                    .trim()
                                    .takeIf {
                                        it.isNotBlank()
                                    }
                                    ?.toRequestBody(
                                        "text/plain".toMediaType()
                                    )

                            val verificationRequest =
                                returnedVerificationId
                                    .toRequestBody(
                                        "text/plain".toMediaType()
                                    )

                            val profileResponse =
                                RetrofitClient.api
                                    .createOrGetVerifiedUser(

                                        name = nameRequest,

                                        phone = phoneRequest,

                                        address = addressRequest,

                                        verificationId =
                                            verificationRequest
                                    )

                            if (profileResponse.success) {

                                userProfileId =
                                    profileResponse.user_profile_id

                                navController.navigate(
                                    Screen.Home.route
                                ) {

                                    popUpTo(
                                        Screen.Language.route
                                    ) {
                                        inclusive = true
                                    }
                                }

                            } else {

                                Toast.makeText(
                                    context,
                                    "Unable to create verified profile",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } catch (e: Exception) {

                            Toast.makeText(
                                context,
                                "Verification failed: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },

                onResend = {

                    scope.launch {

                        try {

                            val response =
                                RetrofitClient.api.sendOtp(
                                    userPhone.trim()
                                )

                            if (response.success) {

                                devOtp =
                                    response.dev_otp

                                verificationId =
                                    response.verification_id

                                Toast.makeText(
                                    context,
                                    "New OTP generated",
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {

                                Toast.makeText(
                                    context,
                                    response.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } catch (e: Exception) {

                            Toast.makeText(
                                context,
                                "Unable to resend OTP",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
        }

        // --------------------------------------------------
        // HOME
        // --------------------------------------------------

        composable(Screen.Home.route) {

            val profileId = userProfileId

            if (profileId != null) {

                MainScaffold(

                    userName = userName,

                    userPhone = userPhone,

                    userAddress = userAddress,

                    userProfileId = profileId
                )
            }
        }
    }
}