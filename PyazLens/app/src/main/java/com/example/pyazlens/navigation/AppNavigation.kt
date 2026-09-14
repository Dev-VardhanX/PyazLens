package com.example.pyazlens.navigation

import android.app.Activity
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
import com.example.pyazlens.data.language.LanguageManager
import com.example.pyazlens.data.network.RetrofitClient
import com.example.pyazlens.data.profile.ProfileManager
import com.example.pyazlens.ui.language.LanguageScreen
import com.example.pyazlens.ui.main.MainScaffold
import com.example.pyazlens.ui.otp.OtpScreen
import com.example.pyazlens.ui.splash.SplashScreen
import com.example.pyazlens.ui.userdetails.UserDetailsScreen
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val firebaseAuth = remember {
        FirebaseAuth.getInstance()
    }

    // --------------------------------------------------
    // USER STATE
    // --------------------------------------------------

    var userName by remember {
        mutableStateOf("")
    }

    var userPhone by remember {
        mutableStateOf("")
    }

    var userAddress by remember {
        mutableStateOf("")
    }

    var userProfileId by remember {
        mutableStateOf<Long?>(null)
    }

    var currentLanguage by remember {
        mutableStateOf(LanguageManager.getSavedLanguage(context))
    }

    // --------------------------------------------------
    // FIREBASE OTP STATE
    // --------------------------------------------------

    var verificationId by remember {
        mutableStateOf<String?>(null)
    }

    var resendToken by remember {
        mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null)
    }

    // --------------------------------------------------
    // AUTHENTICATE WITH FASTAPI USING FIREBASE TOKEN
    // --------------------------------------------------

    fun authenticateWithBackend() {

        scope.launch {

            try {

                // Get Firebase ID token
                val firebaseUser =
                    firebaseAuth.currentUser

                if (firebaseUser == null) {

                    Toast.makeText(
                        context,
                        "Firebase user not found.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@launch
                }

                val tokenResult =
                    firebaseUser
                        .getIdToken(false)
                        .await()

                val idToken =
                    tokenResult.token

                if (idToken.isNullOrBlank()) {

                    Toast.makeText(
                        context,
                        "Unable to get Firebase ID token.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@launch
                }

                // ------------------------------------------------
                // Send Firebase token to FastAPI
                // ------------------------------------------------

                val response =
                    RetrofitClient.api.authenticateFirebase(

                        idToken = idToken,

                        name = userName,

                        address =
                            userAddress
                                .takeIf {
                                    it.isNotBlank()
                                }
                    )

                if (response.success) {

                    userProfileId =
                        response.user_profile_id

                    userName =
                        response.name.takeIf { !it.isNullOrBlank() } ?: userName

                    userPhone =
                        response.phone ?: userPhone

                    userAddress =
                        response.address ?: userAddress

                    ProfileManager.saveProfile(
                        context = context,
                        profileId = response.user_profile_id,
                        name = userName,
                        phone = userPhone,
                        address = userAddress
                    )

                    Toast.makeText(
                        context,
                        "Profile verified successfully.",
                        Toast.LENGTH_SHORT
                    ).show()

                    navController.navigate(
                        Screen.Home.route
                    ) {

                        popUpTo(0) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }

                } else {

                    Toast.makeText(
                        context,
                        "Unable to authenticate with server.",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: HttpException) {

                val errorBody =
                    e.response()
                        ?.errorBody()
                        ?.string()

                Toast.makeText(
                    context,
                    "Server error ${e.code()}: $errorBody",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {

                Toast.makeText(
                    context,
                    "Authentication error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // --------------------------------------------------
    // SEND FIREBASE OTP
    // --------------------------------------------------

    fun sendFirebaseOtp(
        phone: String,
        isResend: Boolean = false
    ) {

        val activity =
            context as? Activity

        if (activity == null) {

            Toast.makeText(
                context,
                "Unable to start phone verification.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val cleanPhone =
            phone.trim()

        if (cleanPhone.isBlank()) {

            Toast.makeText(
                context,
                "Phone number is required.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val builder =
            PhoneAuthOptions
                .newBuilder(firebaseAuth)
                .setPhoneNumber(cleanPhone)
                .setTimeout(
                    60L,
                    TimeUnit.SECONDS
                )
                .setActivity(activity)
                .setCallbacks(

                    object :
                        PhoneAuthProvider
                        .OnVerificationStateChangedCallbacks() {

                        // ------------------------------------------
                        // AUTOMATIC VERIFICATION
                        // ------------------------------------------

                        override fun onVerificationCompleted(
                            credential: PhoneAuthCredential
                        ) {

                            scope.launch {

                                try {

                                    firebaseAuth
                                        .signInWithCredential(
                                            credential
                                        )
                                        .await()

                                    Toast.makeText(
                                        context,
                                        "Phone verified successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    authenticateWithBackend()

                                } catch (e: Exception) {

                                    Toast.makeText(
                                        context,
                                        "Automatic verification failed: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }

                        // ------------------------------------------
                        // VERIFICATION FAILED
                        // ------------------------------------------

                        override fun onVerificationFailed(
                            e: FirebaseException
                        ) {

                            Toast.makeText(
                                context,
                                "OTP error: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        // ------------------------------------------
                        // OTP SENT
                        // ------------------------------------------

                        override fun onCodeSent(
                            newVerificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {

                            verificationId =
                                newVerificationId

                            resendToken =
                                token

                            if (!isResend) {

                                navController.navigate(
                                    Screen.Otp.route
                                )

                            } else {

                                Toast.makeText(
                                    context,
                                    "New OTP sent.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )

        if (
            isResend &&
            resendToken != null
        ) {

            builder.setForceResendingToken(
                resendToken!!
            )
        }

        PhoneAuthProvider.verifyPhoneNumber(
            builder.build()
        )
    }

    // --------------------------------------------------
    // STARTUP / SESSION ROUTING DECISION
    // --------------------------------------------------

    fun proceedAfterLanguageSelection() {
        val currentUser = firebaseAuth.currentUser
        val hasProfile = ProfileManager.hasSavedProfile(context)

        if (currentUser != null && hasProfile) {
            userName = ProfileManager.getUserName(context)
            userPhone = ProfileManager.getUserPhone(context)
            userAddress = ProfileManager.getUserAddress(context)
            userProfileId = ProfileManager.getProfileId(context)

            navController.navigate(Screen.Home.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        } else if (currentUser != null && !hasProfile) {
            scope.launch {
                try {
                    val tokenResult = currentUser.getIdToken(false).await()
                    val idToken = tokenResult.token
                    if (!idToken.isNullOrBlank()) {
                        val response = RetrofitClient.api.authenticateFirebase(
                            idToken = idToken,
                            name = "",
                            address = null
                        )
                        if (response.success && response.user_profile_id > 0L) {
                            val pId = response.user_profile_id
                            val name = response.name.takeIf { !it.isNullOrBlank() } ?: "User"
                            val phone = response.phone ?: ""
                            val addr = response.address ?: ""

                            ProfileManager.saveProfile(context, pId, name, phone, addr)
                            userProfileId = pId
                            userName = name
                            userPhone = phone
                            userAddress = addr

                            navController.navigate(Screen.Home.route) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                            return@launch
                        }
                    }
                } catch (_: Exception) {}

                navController.navigate(Screen.UserDetails.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        } else {
            navController.navigate(Screen.UserDetails.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // --------------------------------------------------
    // NAVIGATION
    // --------------------------------------------------

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // --------------------------------------------------
        // SPLASH
        // --------------------------------------------------

        composable(
            Screen.Splash.route
        ) {

            SplashScreen(

                onSplashFinished = {
                    val hasLanguage = LanguageManager.hasSavedLanguage(context)
                    if (!hasLanguage) {
                        navController.navigate(
                            Screen.Language.route
                        ) {
                            popUpTo(
                                Screen.Splash.route
                            ) {
                                inclusive = true
                            }
                        }
                    } else {
                        proceedAfterLanguageSelection()
                    }
                }
            )
        }

        // --------------------------------------------------
        // LANGUAGE
        // --------------------------------------------------

        composable(
            Screen.Language.route
        ) {

            LanguageScreen(
                currentLanguage = currentLanguage,
                onContinue = { selectedLangCode ->
                    LanguageManager.saveLanguage(context, selectedLangCode)
                    currentLanguage = selectedLangCode
                    proceedAfterLanguageSelection()
                }
            )
        }

        // --------------------------------------------------
        // USER DETAILS
        // --------------------------------------------------

        composable(
            Screen.UserDetails.route
        ) {

            UserDetailsScreen(
                currentLanguage = currentLanguage,
                onBack = {

                    navController.popBackStack()
                },

                onContinue = {
                        fullName,
                        phone,
                        location ->

                    userName =
                        fullName.trim()

                    userPhone =
                        phone.trim()

                    userAddress =
                        location.trim()

                    // ------------------------------------------
                    // NO PHONE
                    // ------------------------------------------

                    if (phone.isBlank()) {

                        scope.launch {

                            try {

                                var firebaseUser = firebaseAuth.currentUser

                                if (firebaseUser == null) {
                                    firebaseAuth
                                        .signInAnonymously()
                                        .await()
                                    firebaseUser = firebaseAuth.currentUser
                                }

                                if (firebaseUser == null) {
                                    throw Exception("Firebase user authentication failed.")
                                }

                                val tokenResult =
                                    firebaseUser
                                        .getIdToken(false)
                                        .await()

                                val idToken =
                                    tokenResult.token
                                        ?: throw Exception(
                                            "Unable to get Firebase ID token."
                                        )

                                val response =
                                    RetrofitClient.api.authenticateFirebase(
                                        idToken = idToken,
                                        name = fullName.trim(),
                                        address = location
                                            .trim()
                                            .takeIf {
                                                it.isNotBlank()
                                            }
                                    )

                                if (response.success) {

                                    userProfileId =
                                        response.user_profile_id

                                    userName =
                                        response.name.takeIf { !it.isNullOrBlank() } ?: fullName

                                    userPhone =
                                        response.phone ?: ""

                                    userAddress =
                                        response.address ?: location

                                    ProfileManager.saveProfile(
                                        context = context,
                                        profileId = response.user_profile_id,
                                        name = userName,
                                        phone = userPhone,
                                        address = userAddress
                                    )

                                    navController.navigate(
                                        Screen.Home.route
                                    ) {

                                        popUpTo(0) {
                                            inclusive = true
                                        }

                                        launchSingleTop = true
                                    }

                                } else {

                                    Toast.makeText(
                                        context,
                                        "Unable to create user profile.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            } catch (e: Exception) {

                                Toast.makeText(
                                    context,
                                    "Authentication error: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {

                        // ------------------------------------------
                        // PHONE PROVIDED → FIREBASE OTP
                        // ------------------------------------------

                        sendFirebaseOtp(
                            phone = phone.trim()
                        )
                    }
                }
            )
        }

        // --------------------------------------------------
        // OTP
        // --------------------------------------------------

        composable(
            Screen.Otp.route
        ) {

            OtpScreen(
                phone = userPhone,
                currentLanguage = currentLanguage,
                onBack = {

                    navController.popBackStack()
                },

                // ------------------------------------------
                // VERIFY OTP
                // ------------------------------------------

                onVerify = { otp ->

                    val currentVerificationId =
                        verificationId

                    if (
                        currentVerificationId
                            .isNullOrBlank()
                    ) {

                        Toast.makeText(
                            context,
                            "Verification session expired. Please request a new OTP.",
                            Toast.LENGTH_LONG
                        ).show()

                        return@OtpScreen
                    }

                    val credential =
                        PhoneAuthProvider.getCredential(
                            currentVerificationId,
                            otp.trim()
                        )

                    scope.launch {

                        try {

                            firebaseAuth
                                .signInWithCredential(
                                    credential
                                )
                                .await()

                            Toast.makeText(
                                context,
                                "Phone verified successfully.",
                                Toast.LENGTH_SHORT
                            ).show()

                            // ----------------------------------
                            // SEND FIREBASE TOKEN TO FASTAPI
                            // ----------------------------------

                            authenticateWithBackend()

                        } catch (e: Exception) {

                            Toast.makeText(
                                context,
                                "Invalid OTP: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },

                // ------------------------------------------
                // RESEND OTP
                // ------------------------------------------

                onResend = {

                    sendFirebaseOtp(
                        phone = userPhone.trim(),
                        isResend = true
                    )
                }
            )
        }

        // --------------------------------------------------
        // HOME
        // --------------------------------------------------

        composable(
            Screen.Home.route
        ) {

            val profileId =
                userProfileId

            if (profileId != null) {

                MainScaffold(
                    userName = userName,
                    userPhone = userPhone,
                    userAddress = userAddress,
                    userProfileId = profileId,
                    currentLanguage = currentLanguage,
                    onLanguageChanged = { newLang ->
                        LanguageManager.saveLanguage(context, newLang)
                        currentLanguage = newLang
                    },
                    onLogout = {
                        scope.launch {
                            try {
                                firebaseAuth.signOut()
                            } catch (_: Exception) {}
                            ProfileManager.clearProfile(context)
                            userProfileId = null
                            userName = ""
                            userPhone = ""
                            userAddress = ""

                            navController.navigate(Screen.UserDetails.route) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}