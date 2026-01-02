package com.iwatdigital.dropdroid.data.db

import androidx.room.TypeConverter
import com.iwatdigital.dropdroid.core.model.TransportType

class RoomConverters {
    @TypeConverter
    fun toTransport(value: String?): TransportType? = value?.let { TransportType.valueOf(it) }

    @TypeConverter
    fun fromTransport(value: TransportType?): String? = value?.name
}
