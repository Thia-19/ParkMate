package com.example.parkmate.core.domain.util.images

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun createImageUri(context: Context): Uri {
    val directory = File(context.getExternalFilesDir(null), "Pictures")
    if (!directory.exists()) {
        directory.mkdirs()
    }
    val file = File.createTempFile(
        "parking_${System.currentTimeMillis()}_",
        ".jpg",
        directory
    )
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}