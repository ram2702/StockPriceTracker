package com.personal.realtimepricetracker.data.db

import androidx.room.TypeConverter
import com.personal.realtimepricetracker.data.model.DailyData
import com.personal.realtimepricetracker.data.model.StockPricePoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class Converters {
    @TypeConverter
    fun fromStockPricePointList(value: Map<String,DailyData>): String =
        Json.encodeToString(value)

    @TypeConverter
    fun toStockPricePointList(value: String): Map<String,DailyData> =
        Json.decodeFromString(value)

    @TypeConverter
    fun uuidToString(uuid: UUID):String = uuid.toString()

    @TypeConverter
    fun stringToUuid(uuid: String):UUID = UUID.fromString(uuid)

}