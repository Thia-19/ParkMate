package com.example.parkmate.feature_home.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import androidx.core.graphics.createBitmap
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.MarkerIconType

fun bitmapDescriptorFromVector(
    context: Context,
    @DrawableRes vectorResId: Int
): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null

    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

    val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

val MarkerIconType.iconRes: Int
    get() = when (this) {
        MarkerIconType.HOME -> R.drawable.ic_saved_location_home_marker
        MarkerIconType.JOB -> R.drawable.ic_saved_location_job_marker
        MarkerIconType.SCHOOL -> R.drawable.ic_saved_location_school_marker
        MarkerIconType.SHOPPING_CENTRE -> R.drawable.ic_saved_location_shopping_marker
        MarkerIconType.BAR -> R.drawable.ic_saved_location_bar_marker
        MarkerIconType.LIBRARY -> R.drawable.ic_saved_location_library_marker
        MarkerIconType.HOSPITAL -> R.drawable.ic_saved_location_hospital_marker
        MarkerIconType.TRAIN_STATION -> R.drawable.ic_saved_location_station_marker
        MarkerIconType.AIRPORT -> R.drawable.ic_saved_location_airport_marker
    }