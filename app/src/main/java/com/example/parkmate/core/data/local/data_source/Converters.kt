package com.example.parkmate.core.data.local.data_source

import androidx.room.TypeConverter
import com.example.parkmate.core.domain.models.ParkingType

// The latter is an utility function used to help Room convert
// from type parkingType to String and viceversa
class Converters {
    @TypeConverter
    fun fromParkingType(value: ParkingType): String{
        return value.name
    }

    @TypeConverter
    fun toParkingType(value: String): ParkingType{
        return ParkingType.valueOf(value)
    }
}