package com.alon.plantpulse.plantcare.data.local.converters

import androidx.room.TypeConverter
import com.alon.plantpulse.plantcare.application.model.AlertSettings
import com.alon.plantpulse.plantcare.application.model.NotificationSound
import com.alon.plantpulse.plantcare.application.model.RecurrencePattern
import com.google.gson.*
import java.lang.reflect.Type
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Room type converters for plant care data models.
 * Handles serialization of polymorphic types (sealed classes) using Gson to avoid instantiation errors.
 */
class PlantCareConverters {
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(RecurrencePattern::class.java, RecurrencePatternAdapter())
        .registerTypeAdapter(NotificationSound::class.java, NotificationSoundAdapter())
        .create()

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
    }

    @TypeConverter
    fun fromRecurrencePattern(value: RecurrencePattern): String {
        return gson.toJson(value, RecurrencePattern::class.java)
    }

    @TypeConverter
    fun toRecurrencePattern(value: String): RecurrencePattern {
        return gson.fromJson(value, RecurrencePattern::class.java)
    }

    @TypeConverter
    fun fromAlertSettings(value: AlertSettings): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toAlertSettings(value: String): AlertSettings {
        return gson.fromJson(value, AlertSettings::class.java)
    }

    private class RecurrencePatternAdapter : JsonSerializer<RecurrencePattern>, JsonDeserializer<RecurrencePattern> {
        override fun serialize(src: RecurrencePattern, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            val jsonObject = context.serialize(src, src.javaClass).asJsonObject
            jsonObject.addProperty("type", src.javaClass.simpleName)
            return jsonObject
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): RecurrencePattern {
            val jsonObject = json.asJsonObject
            val type = jsonObject.get("type")?.asString
            return when (type) {
                RecurrencePattern.None::class.java.simpleName -> RecurrencePattern.None
                RecurrencePattern.WeeklyRoutine::class.java.simpleName -> context.deserialize(jsonObject, RecurrencePattern.WeeklyRoutine::class.java)
                RecurrencePattern.FixedInterval::class.java.simpleName -> context.deserialize(jsonObject, RecurrencePattern.FixedInterval::class.java)
                else -> throw JsonParseException("Unknown RecurrencePattern type: $type")
            }
        }
    }

    private class NotificationSoundAdapter : JsonSerializer<NotificationSound>, JsonDeserializer<NotificationSound> {
        override fun serialize(src: NotificationSound, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            val jsonObject = context.serialize(src, src.javaClass).asJsonObject
            jsonObject.addProperty("type", src.javaClass.simpleName)
            return jsonObject
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): NotificationSound {
            val jsonObject = json.asJsonObject
            val type = jsonObject.get("type")?.asString
            return when (type) {
                NotificationSound.Default::class.java.simpleName -> NotificationSound.Default
                NotificationSound.Custom::class.java.simpleName -> context.deserialize(jsonObject, NotificationSound.Custom::class.java)
                else -> throw JsonParseException("Unknown NotificationSound type: $type")
            }
        }
    }
}
