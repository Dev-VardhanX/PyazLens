package com.example.pyazlens.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.pyazlens.data.network.AnalyzeResponse
import com.example.pyazlens.data.network.RetrofitClient
import com.example.pyazlens.data.network.uriToMultipart
import com.example.pyazlens.ui.theme.PyazLensTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody

private val Purple = Color(0xFF511D50)
private val Green = Color(0xFF73C943)

@Composable
fun ScanScreen(
    initialImageUri: Uri? = null,
    userName: String,
    userPhone: String,
    userAddress: String,
    userProfileId: Long,
    onAnalysisComplete: (AnalyzeResponse) -> Unit
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // ------------------------------------------------
    // Camera state
    // ------------------------------------------------

    var imageCapture by remember {
        mutableStateOf<ImageCapture?>(null)
    }

    var camera by remember {
        mutableStateOf<Camera?>(null)
    }

    // ------------------------------------------------
    // Captured image
    // ------------------------------------------------

    var capturedBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    // ------------------------------------------------
    // Gallery image
    // ------------------------------------------------

    var galleryBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var galleryImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    // Load image coming from Home → Upload
    LaunchedEffect(initialImageUri) {

        if (initialImageUri != null) {

            try {

                val bitmap =
                    context.contentResolver
                        .openInputStream(initialImageUri)
                        ?.use {
                            BitmapFactory.decodeStream(it)
                        }

                if (bitmap != null) {

                    galleryBitmap = bitmap
                    galleryImageUri = initialImageUri
                    capturedBitmap = null
                }

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }

    // ------------------------------------------------
    // Flash
    // ------------------------------------------------

    var flashEnabled by remember {
        mutableStateOf(false)
    }

    // ------------------------------------------------
    // AI
    // ------------------------------------------------

    var isAnalyzing by remember {
        mutableStateOf(false)
    }

    var analysisError by remember {
        mutableStateOf<String?>(null)
    }

    // ------------------------------------------------
    // Camera permission
    // ------------------------------------------------

    var hasCameraPermission by remember {

        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasCameraPermission = granted
        }

    LaunchedEffect(Unit) {

        if (!hasCameraPermission) {

            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    // ------------------------------------------------
    // Gallery
    // ------------------------------------------------

    val galleryLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            if (uri != null) {

                try {

                    val bitmap =
                        context.contentResolver
                            .openInputStream(uri)
                            ?.use {
                                BitmapFactory.decodeStream(it)
                            }

                    if (bitmap != null) {

                        galleryBitmap = bitmap
                        galleryImageUri = uri
                        capturedBitmap = null
                        analysisError = null
                    }

                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }
        }

    // ------------------------------------------------
    // CameraX setup
    // ------------------------------------------------

    fun startCamera(previewView: PreviewView) {

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            try {

                val cameraProvider =
                    cameraProviderFuture.get()

                val preview =
                    CameraPreview.Builder()
                        .build()

                val newImageCapture =
                    ImageCapture.Builder()
                        .setCaptureMode(
                            ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
                        )
                        .build()

                val cameraSelector =
                    CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()

                val newCamera =
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        newImageCapture
                    )

                preview.setSurfaceProvider(
                    previewView.surfaceProvider
                )

                imageCapture = newImageCapture
                camera = newCamera

            } catch (e: Exception) {

                e.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(context))
    }

    // ------------------------------------------------
    // Capture photo
    // ------------------------------------------------

    fun capturePhoto() {

        val capture =
            imageCapture ?: return

        val photoFile =
            java.io.File(
                context.cacheDir,
                "pyazlens_${System.currentTimeMillis()}.jpg"
            )

        val outputOptions =
            ImageCapture.OutputFileOptions
                .Builder(photoFile)
                .build()

        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),

            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(
                    outputFileResults: ImageCapture.OutputFileResults
                ) {

                    try {

                        val bitmap =
                            BitmapFactory.decodeFile(
                                photoFile.absolutePath
                            )

                        if (bitmap != null) {

                            capturedBitmap = bitmap
                            galleryBitmap = null
                            galleryImageUri = null
                            analysisError = null
                        }

                    } catch (e: Exception) {

                        e.printStackTrace()
                    }
                }

                override fun onError(
                    exception: ImageCaptureException
                ) {

                    exception.printStackTrace()

                    analysisError =
                        "Unable to capture image."
                }
            }
        )
    }

    // ------------------------------------------------
    // Analyze image
    // ------------------------------------------------

    fun analyzeImage() {

        val imageUri: Uri? =

            galleryImageUri
                ?: try {

                    if (capturedBitmap == null) {
                        null
                    } else {

                        val file =
                            java.io.File(
                                context.cacheDir,
                                "pyazlens_upload_${System.currentTimeMillis()}.jpg"
                            )

                        file.outputStream().use { outputStream ->

                            capturedBitmap?.compress(
                                Bitmap.CompressFormat.JPEG,
                                90,
                                outputStream
                            )
                        }

                        Uri.fromFile(file)
                    }

                } catch (e: Exception) {

                    e.printStackTrace()
                    null
                }

        if (imageUri == null) {

            analysisError =
                "Unable to prepare image."

            return
        }

        isAnalyzing = true
        analysisError = null

        MainScope().launch {

            try {

                // Convert selected/captured image to multipart
                val filePart =
                    uriToMultipart(
                        context,
                        imageUri
                    )

                // ------------------------------------------------
                // ACTUAL USER DETAILS
                // ------------------------------------------------

                val name =
                    userName
                        .trim()
                        .toRequestBody()

                val phone =
                    userPhone
                        .trim()
                        .takeIf {
                            it.isNotBlank()
                        }
                        ?.toRequestBody()

                val address =
                    userAddress
                        .trim()
                        .takeIf {
                            it.isNotBlank()
                        }
                        ?.toRequestBody()

                // ------------------------------------------------
                // USER PROFILE ID
                // ------------------------------------------------

                val profileId =
                    userProfileId
                        .toString()
                        .toRequestBody()

                // ------------------------------------------------
                // API CALL
                // ------------------------------------------------

                val response =
                    RetrofitClient.api.analyzeImage(

                        file = filePart,

                        name = name,

                        phone = phone,

                        address = address
                    )

                isAnalyzing = false

                if (response.success) {

                    onAnalysisComplete(response)

                } else {

                    analysisError =
                        "AI analysis failed."
                }

            } catch (e: Exception) {

                e.printStackTrace()

                isAnalyzing = false

                analysisError =
                    "Connection failed: ${e.message}"
            }
        }
    }

    // ------------------------------------------------
    // Current image
    // ------------------------------------------------

    val currentBitmap =
        capturedBitmap ?: galleryBitmap

    // ------------------------------------------------
    // UI
    // ------------------------------------------------

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // =================================================
        // CAMERA PREVIEW
        // =================================================

        if (
            currentBitmap == null &&
            hasCameraPermission
        ) {

            AndroidView(
                factory = { ctx ->

                    PreviewView(ctx).apply {

                        layoutParams =
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )

                        scaleType =
                            PreviewView.ScaleType.FILL_CENTER

                        startCamera(this)
                    }
                },

                modifier =
                    Modifier.fillMaxSize()
            )
        }

        // =================================================
        // CAPTURED / GALLERY IMAGE
        // =================================================

        if (currentBitmap != null) {

            Image(
                bitmap =
                    currentBitmap.asImageBitmap(),

                contentDescription =
                    "Onion inspection image",

                modifier =
                    Modifier.fillMaxSize(),

                contentScale =
                    ContentScale.Crop
            )
        }

        // =================================================
        // NO CAMERA PERMISSION
        // =================================================

        if (
            !hasCameraPermission &&
            currentBitmap == null
        ) {

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Color(0xFF302A2F)
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "📷",
                        fontSize = 70.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )

                    Text(
                        text =
                            "Camera permission required",

                        color = Color.White,

                        fontSize = 17.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }

        // =================================================
        // TOP INSTRUCTION
        // =================================================

        Box(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(
                        top = 40.dp,
                        start = 24.dp,
                        end = 24.dp
                    )
                    .clip(
                        RoundedCornerShape(16.dp)
                    )
                    .background(
                        Color.Black.copy(
                            alpha = 0.70f
                        )
                    )
                    .padding(
                        horizontal = 18.dp,
                        vertical = 12.dp
                    )
        ) {

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text =
                        if (currentBitmap == null)
                            "Position the onions"
                        else
                            "Image captured",

                    color = Color.White,

                    fontSize = 17.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        if (currentBitmap == null)
                            "Keep all onions inside the frame"
                        else
                            "Ready for AI inspection",

                    color =
                        Color(0xFFD9D0DB),

                    fontSize = 12.sp,

                    textAlign =
                        TextAlign.Center
                )
            }
        }

        // =================================================
        // SCANNING FRAME
        // =================================================

        if (currentBitmap == null) {

            Box(
                modifier =
                    Modifier
                        .size(310.dp)
                        .align(Alignment.Center)
                        .border(
                            width = 3.dp,
                            color = Green,
                            shape =
                                RoundedCornerShape(
                                    32.dp
                                )
                        )
            )
        }

        // =================================================
        // CLEAR IMAGE
        // =================================================

        if (currentBitmap != null) {

            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(
                            top = 40.dp,
                            end = 24.dp
                        )
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Color.Black.copy(
                                alpha = 0.7f
                            )
                        )
                        .clickable {

                            if (!isAnalyzing) {

                                capturedBitmap = null
                                galleryBitmap = null
                                galleryImageUri = null
                                analysisError = null
                            }
                        },

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Close,

                    contentDescription =
                        "Clear image",

                    tint =
                        Color.White,

                    modifier =
                        Modifier.size(24.dp)
                )
            }
        }

        // =================================================
        // ERROR MESSAGE
        // =================================================

        if (
            analysisError != null &&
            !isAnalyzing
        ) {

            Box(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .padding(
                            horizontal = 32.dp
                        )
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
                        .background(
                            Color.Black.copy(
                                alpha = 0.85f
                            )
                        )
                        .padding(20.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        analysisError!!,

                    color = Color.White,

                    fontSize = 15.sp,

                    fontWeight =
                        FontWeight.Medium,

                    textAlign =
                        TextAlign.Center
                )
            }
        }

        // =================================================
        // BOTTOM CONTROLS
        // =================================================

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(
                        start = 28.dp,
                        end = 28.dp,
                        bottom = 36.dp
                    ),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            // ------------------------------------------------
            // Gallery
            // ------------------------------------------------

            Box(
                modifier =
                    Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Color.White.copy(
                                alpha = 0.25f
                            )
                        )
                        .clickable {

                            if (!isAnalyzing) {

                                galleryLauncher.launch(
                                    "image/*"
                                )
                            }
                        },

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Collections,

                    contentDescription =
                        "Gallery",

                    tint =
                        Color.White,

                    modifier =
                        Modifier.size(26.dp)
                )
            }

            // ------------------------------------------------
            // Capture / Analyze
            // ------------------------------------------------

            Box(
                modifier =
                    Modifier
                        .size(82.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {

                            if (isAnalyzing) {
                                return@clickable
                            }

                            if (currentBitmap == null) {

                                capturePhoto()

                            } else {

                                analyzeImage()
                            }
                        }
                        .padding(6.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Purple),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.CameraAlt,

                        contentDescription =
                            if (currentBitmap == null)
                                "Capture"
                            else
                                "Analyze",

                        tint =
                            Color.White,

                        modifier =
                            Modifier.size(30.dp)
                    )
                }
            }

            // ------------------------------------------------
            // Flash
            // ------------------------------------------------

            Box(
                modifier =
                    Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Color.White.copy(
                                alpha = 0.25f
                            )
                        )
                        .clickable {

                            if (!isAnalyzing) {

                                flashEnabled =
                                    !flashEnabled

                                camera
                                    ?.cameraControl
                                    ?.enableTorch(
                                        flashEnabled
                                    )
                            }
                        },

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        if (flashEnabled)
                            Icons.Default.FlashOn
                        else
                            Icons.Default.FlashOff,

                    contentDescription =
                        "Flash",

                    tint =
                        Color.White,

                    modifier =
                        Modifier.size(26.dp)
                )
            }
        }

        // =================================================
        // AI PROCESSING
        // =================================================

        if (isAnalyzing) {

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(
                                alpha = 0.88f
                            )
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    CircularProgressIndicator(
                        color = Green,

                        strokeWidth = 4.dp,

                        modifier =
                            Modifier.size(56.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(24.dp)
                    )

                    Text(
                        text =
                            "Analyzing your onions...",

                        color =
                            Color.White,

                        fontSize = 17.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "AI is inspecting your image",

                        color =
                            Color(0xFFD9D0DB),

                        fontSize = 12.sp,

                        textAlign =
                            TextAlign.Center
                    )
                }
            }
        }
    }
}


// =================================================
// PREVIEW
// =================================================

@Preview(showBackground = true)
@Composable
fun ScanScreenPreview() {

    PyazLensTheme {

        ScanScreen(
            initialImageUri = null,
            userName = "Test User",
            userPhone = "",
            userAddress = "",
            userProfileId = 1L,
            onAnalysisComplete = {}
        )
    }
}