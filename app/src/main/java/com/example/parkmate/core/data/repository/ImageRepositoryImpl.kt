package com.example.parkmate.core.data.repository

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.example.parkmate.core.domain.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageRepository {

    override suspend fun saveImage(uriString: String): String? = withContext(Dispatchers.IO) {
        try {
            val uri = uriString.toUri()
            val fileName = "${IMAGE_FILE_PREFIX}${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Errore durante il salvataggio dell'immagine", e)
            null
        }
    }

    override suspend fun deleteImage(path: String) {
        withContext(Dispatchers.IO) {
            try {
                File(path).takeIf { it.exists() }?.delete()
            } catch (e: Exception) {
                Log.e(TAG, "Errore durante l'eliminazione dell'immagine", e)
            }
        }
    }

    private companion object {
        const val TAG = "ImageRepository"
        const val IMAGE_FILE_PREFIX = "image_"
    }
}