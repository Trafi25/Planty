package com.traffipart.polanty.core.common

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun File.toMultipart(): MultipartBody.Part {
    val requestBody = asRequestBody("image/jpeg".toMediaType())
    return MultipartBody.Part.createFormData(name = "images", filename = name, body = requestBody)
}

/**
 * Trims leading and trailing whitespace from the receiving string.
 * If the resulting string is empty or if the receiving string is null, returns null.
 * Otherwise, returns the trimmed string.
 *
 * This utility is useful for cleaning user inputs or AI-generated strings before storage
 * or further business rule validation, treating blank or empty text as absent information.
 *
 * @return The trimmed non-empty [String], or `null` if the string was null, empty, or composed entirely of whitespace.
 */
fun String?.trimToNull(): String? = this?.trim()?.takeIf { it.isNotEmpty() }
