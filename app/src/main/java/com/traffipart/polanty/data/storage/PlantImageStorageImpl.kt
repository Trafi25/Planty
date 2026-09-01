package com.traffipart.polanty.data.storage

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.traffipart.polanty.domain.storage.PlantImageStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

class PlantImageStorageImpl
    @Inject
    constructor(
        @ApplicationContext
        private val context: Context,
    ) : PlantImageStorage {
        override suspend fun saveImage(imageUri: String): String =
            withContext(Dispatchers.IO) {
                val uri = imageUri.toUri()

                val mimeType = context.contentResolver.getType(uri)
                val extension =
                    when (mimeType) {
                        "image/png" -> "png"
                        "image/webp" -> "webp"
                        else -> "jpg"
                    }
                val imageDirectory =
                    File(
                        context.filesDir,
                        "plant_images",
                    ).apply {
                        mkdirs()
                    }
                val imageFile = File(imageDirectory, "${UUID.randomUUID()}.$extension")

                val inputStream = context.contentResolver.openInputStream(uri) ?: throw IOException("Could not open input stream")

                inputStream.use { input ->
                    imageFile.outputStream().use { outputStream -> input.copyTo(outputStream) }
                }

                Uri.fromFile(imageFile).toString()
            }

        override suspend fun deleteImage(imageUri: String) {
            withContext(Dispatchers.IO) {
                val uri = imageUri.toUri()
                if (uri.scheme != "file") {
                    return@withContext
                }
                val path = uri.path ?: return@withContext
                val imagesDirectory =
                    File(
                        context.filesDir,
                        "plant_images",
                    ).canonicalFile
                val imageFile =
                    File(path).canonicalFile
                if (
                    imageFile.path.startsWith(
                        imagesDirectory.path,
                    )
                ) {
                    imageFile.delete()
                }
            }
        }
    }
