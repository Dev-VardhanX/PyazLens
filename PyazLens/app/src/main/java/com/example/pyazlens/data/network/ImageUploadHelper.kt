package com.example.pyazlens.data.network

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.BufferedInputStream
import java.io.File

private const val MAX_UPLOAD_BYTES = 12 * 1024 * 1024

fun uriToMultipart(
    context: Context,
    uri: Uri
): MultipartBody.Part {

    val bytes: ByteArray

    if (uri.scheme == "content") {

        val declaredSize = context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length }
        if (declaredSize != null && declaredSize > MAX_UPLOAD_BYTES) {
            throw Exception("Image is too large. Choose an image under 12 MB.")
        }

        bytes = context.contentResolver.openInputStream(uri)?.use { stream ->
            BufferedInputStream(stream).readBytesLimited(MAX_UPLOAD_BYTES)
        } ?: throw Exception("Unable to open image")

    } else {

        val file =
            File(uri.path ?: throw Exception("Invalid image path"))

        if (!file.exists()) {
            throw Exception("Image file does not exist")
        }

        if (file.length() > MAX_UPLOAD_BYTES) throw Exception("Image is too large. Choose an image under 12 MB.")
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

private fun BufferedInputStream.readBytesLimited(limit: Int): ByteArray {
    val output = java.io.ByteArrayOutputStream()
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    while (true) {
        val count = read(buffer)
        if (count < 0) break
        if (output.size() + count > limit) throw Exception("Image is too large. Choose an image under 12 MB.")
        output.write(buffer, 0, count)
    }
    return output.toByteArray()
}
