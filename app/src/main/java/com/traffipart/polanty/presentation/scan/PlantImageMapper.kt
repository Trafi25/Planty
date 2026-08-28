package com.traffipart.polanty.presentation.scan

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.traffipart.polanty.domain.model.PlantImage

fun Uri.toPlantImage(context: Context): PlantImage? {
    val contentResolver = context.contentResolver
    val mimeType = contentResolver.getType(this) ?: return null
    val bytes =
        contentResolver.openInputStream(this)?.use { inputStream ->
            inputStream.readBytes()
        } ?: return null
    val fileName =
        contentResolver
            .query(
                this,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null,
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(0)
                } else {
                    null
                }
            } ?: "plant_Image"

    return PlantImage(bytes = bytes, fileName = fileName, mimeType = mimeType)
}
