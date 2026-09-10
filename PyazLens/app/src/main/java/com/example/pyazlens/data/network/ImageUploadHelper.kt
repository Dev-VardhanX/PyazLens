package com.example.pyazlens.data.network

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun uriToMultipart(
    context: Context,
    uri: Uri
): MultipartBody.Part {

    val bytes: ByteArray

    if (uri.scheme == "content") {

        val inputStream =
            context.contentResolver.openInputStream(uri)
                ?: throw Exception("Unable to open image")

        bytes = inputStream.use {
            it.readBytes()
        }

    } else {

        val file =
            File(uri.path ?: throw Exception("Invalid image path"))

        if (!file.exists()) {
            throw Exception("Image file does not exist")
        }

        bytes = file.readBytes()
    }

    if (bytes.isEmpty()) {
        throw Exception("Image file is empty")
    }

    val requestBody =
        bytes.toRequestBody(
            "image/jpeg".toMediaType()
        )

    return MultipartBody.Part.createFormData(
        name = "file",
        filename = "pyazlens_image.jpg",
        body = requestBody
    )
}