package com.example.parkmate.di

import com.example.parkmate.core.domain.util.map.AppGeocoder
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppGeocoderEntryPoint {
    fun appGeocoder(): AppGeocoder
}