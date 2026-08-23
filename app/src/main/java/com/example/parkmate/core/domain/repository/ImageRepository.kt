package com.example.parkmate.core.domain.repository
import android.net.Uri

interface ImageRepository {
    suspend fun saveImage(uriString: String): String?
    suspend fun deleteImage(path: String)
}