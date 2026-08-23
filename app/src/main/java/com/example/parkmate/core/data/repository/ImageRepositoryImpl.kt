package com.example.parkmate.core.data.repository

import android.content.Context
import android.util.Log
import com.example.parkmate.core.domain.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import androidx.core.net.toUri

class ImageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageRepository {

    override suspend fun saveImage(uriString: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val uri = uriString.toUri()
                val fileName = "vehicle_${UUID.randomUUID()}.jpg"
                val file = File(context.filesDir, fileName)

                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                file.absolutePath
            } catch (e: Exception) {
                Log.e("ImageRepository", "Errore durante il salvataggio dell'immagine", e)
                null
            }
        }
    }

    override suspend fun deleteImage(path: String) {
        withContext(Dispatchers.IO) {
            try {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                Log.e("ImageRepository", "Errore durante l'eliminazione dell'immagine", e)
            }
        }
    }
}