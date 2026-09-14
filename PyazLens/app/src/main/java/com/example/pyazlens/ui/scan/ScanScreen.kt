package com.example.pyazlens.ui.scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.ViewGroup
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.pyazlens.data.language.AppStrings
import com.example.pyazlens.data.network.AnalyzeResponse
import com.example.pyazlens.data.network.uriToMultipart
import com.example.pyazlens.data.network.RetrofitClient
import com.example.pyazlens.ui.theme.PyazGreen
import com.example.pyazlens.ui.theme.PyazLensTheme
import com.example.pyazlens.ui.theme.PyazPurple
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream

@Composable
fun ScanScreen(
    initialImageUri: Uri? = null,
    userName: String,
    userPhone: String,
    userAddress: String,
    userProfileId: Long,
    currentLanguage: String = "en",
    onAnalysisComplete: (AnalyzeResponse) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val strings = AppStrings.getStrings(currentLanguage)

    // Camera state
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var camera by remember { mutableStateOf<Camera?>(null) }

    // Captured / Gallery Image Bitmaps & Uris
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var galleryBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var galleryImageUri by remember { mutableStateOf<Uri?>(null) }

    // Load initial image coming from Home → Upload
    LaunchedEffect(initialImageUri) {
        if (initialImageUri != null) {
            try {
                val bitmap = context.contentResolver.openInputStream(initialImageUri)?.use {
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

    var flashEnabled by remember { mutableStateOf(false) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisError by remember { mutableStateOf<String?>(null) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = context.contentResolver.openInputStream(uri)?.use {
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

    fun startCamera(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = CameraPreview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                imageCapture = ImageCapture.Builder().build()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun capturePhoto() {
        val capture = imageCapture ?: return
        val photoFile = File(context.cacheDir, "scan_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    capturedBitmap = bitmap
                    galleryBitmap = null
                    galleryImageUri = Uri.fromFile(photoFile)
                    analysisError = null
                }

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(context, "Capture failed: ${exc.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    fun analyzeImage() {
        val uri = galleryImageUri
        if (uri == null) {
            val bitmap = capturedBitmap
            if (bitmap == null) {
                Toast.makeText(context, "Please capture or select an image.", Toast.LENGTH_SHORT).show()
                return
            }
            val file = File(context.cacheDir, "temp_scan.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            galleryImageUri = Uri.fromFile(file)
        }

        val imageUri = galleryImageUri ?: return

        isAnalyzing = true
        analysisError = null

        MainScope().launch {
            try {
                val filePart = uriToMultipart(context, imageUri)
                val name = userName.trim().toRequestBody()
                val phone = userPhone.trim().takeIf { it.isNotBlank() }?.toRequestBody()
                val address = userAddress.trim().takeIf { it.isNotBlank() }?.toRequestBody()

                val response = RetrofitClient.api.analyzeImage(
                    file = filePart,
                    name = name,
                    phone = phone,
                    address = address
                )

                isAnalyzing = false
                if (response.success) {
                    onAnalysisComplete(response)
                } else {
                    analysisError = strings.aiAnalysisFailed
                }
            } catch (e: HttpException) {
                isAnalyzing = false
                val errorBody = e.response()?.errorBody()?.string()
                analysisError = if (!errorBody.isNullOrBlank()) {
                    if (errorBody.contains("detail")) {
                        errorBody.substringAfter("\"detail\":\"").substringBefore("\"")
                    } else {
                        errorBody
                    }
                } else {
                    "${strings.connectionFailed} (${e.code()})"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isAnalyzing = false
                analysisError = "${strings.connectionFailed}: ${e.message}"
            }
        }
    }

    val currentBitmap = capturedBitmap ?: galleryBitmap

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // CAMERA PREVIEW
        if (currentBitmap == null && hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        startCamera(this)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // CAPTURED / GALLERY PREVIEW
        if (currentBitmap != null) {
            Image(
                bitmap = currentBitmap.asImageBitmap(),
                contentDescription = strings.readyForAi,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // NO CAMERA PERMISSION
        if (!hasCameraPermission && currentBitmap == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF221625)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📷", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = strings.cameraPermissionTitle,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // TOP INSTRUCTION BANNER
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 36.dp, start = 20.dp, end = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(alpha = 0.78f))
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🪙 ", fontSize = 16.sp)
                    Text(
                        text = if (currentBitmap == null) strings.scanCoinRequired else strings.imageCaptured,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (currentBitmap == null) strings.scanCoinInstruction else strings.readyForAi,
                    color = PyazGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // VIEWFINDER TARGET FRAME
        if (currentBitmap == null) {
            Box(
                modifier = Modifier
                    .size(310.dp)
                    .align(Alignment.Center)
                    .border(
                        width = 3.dp,
                        color = PyazGreen,
                        shape = RoundedCornerShape(28.dp)
                    )
            )
        }

        // CLEAR IMAGE BUTTON
        if (currentBitmap != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, end = 24.dp)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.75f))
                    .clickable {
                        if (!isAnalyzing) {
                            capturedBitmap = null
                            galleryBitmap = null
                            galleryImageUri = null
                            analysisError = null
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = strings.clearImage,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // ERROR OVERLAY
        if (analysisError != null && !isAnalyzing) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 28.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.90f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD94A4A))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "⚠️", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = analysisError!!,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            analysisError = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PyazPurple),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Try Again", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // BOTTOM ACTION CONTROLS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 28.dp, end = 28.dp, bottom = 36.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gallery Picker
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f))
                    .clickable {
                        if (!isAnalyzing) {
                            galleryLauncher.launch("image/*")
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Collections,
                    contentDescription = "Gallery",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Capture / Run AI Inspection Button
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable {
                        if (isAnalyzing) return@clickable
                        if (currentBitmap == null) capturePhoto()
                        else analyzeImage()
                    }
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(if (currentBitmap != null) PyazGreen else PyazPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (currentBitmap == null) Icons.Default.CameraAlt else Icons.Default.AutoAwesome,
                        contentDescription = if (currentBitmap == null) strings.scanBtn else strings.continueBtn,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // Flash Toggle
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f))
                    .clickable {
                        if (!isAnalyzing) {
                            flashEnabled = !flashEnabled
                            camera?.cameraControl?.enableTorch(flashEnabled)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Flash",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // AI PROCESSING OVERLAY
        if (isAnalyzing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.90f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PyazPurple),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = PyazGreen,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = strings.analyzingOnions,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = strings.aiInspectingSub,
                            color = Color(0xFFE4D8E6),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

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
            currentLanguage = "en",
            onAnalysisComplete = {}
        )
    }
}
