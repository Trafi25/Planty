package com.traffipart.polanty.data.remote.plant

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun File.toMultipart(): MultipartBody.Part {
    val requestBody = asRequestBody("image/jpeg".toMediaType())
    return MultipartBody.Part.createFormData(name = "images", filename = name, body = requestBody)
}
