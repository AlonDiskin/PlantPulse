package com.alon.plantpulse.usergarden.data.local

import androidx.room.TypeConverter
import com.alon.plantpulse.usergarden.domain.*

class PlantConverters {
    @TypeConverter
    fun fromPlantCategory(value: PlantCategory?): String? = value?.name

    @TypeConverter
    fun toPlantCategory(value: String?): PlantCategory? = value?.let { enumValueOf<PlantCategory>(it) }

    @TypeConverter
    fun fromBloomingSeason(value: BloomingSeason?): String? = value?.name

    @TypeConverter
    fun toBloomingSeason(value: String?): BloomingSeason? = value?.let { enumValueOf<BloomingSeason>(it) }

    @TypeConverter
    fun fromSunCare(value: PlantSunCare?): String? = value?.name

    @TypeConverter
    fun toSunCare(value: String?): PlantSunCare? = value?.let { enumValueOf<PlantSunCare>(it) }

    @TypeConverter
    fun fromWaterCare(value: PlantWaterCare?): String? = value?.name

    @TypeConverter
    fun toWaterCare(value: String?): PlantWaterCare? = value?.let { enumValueOf<PlantWaterCare>(it) }

    @TypeConverter
    fun fromGerminationDays(value: GerminationDays?): String? = value?.let { "${it.min}-${it.max}" }

    @TypeConverter
    fun toGerminationDays(value: String?): GerminationDays? = value?.split("-")?.let {
        if (it.size == 2) GerminationDays(it[0].toInt(), it[1].toInt()) else null
    }

    @TypeConverter
    fun fromMaturityDays(value: MaturityDays?): String? = value?.let { "${it.min}-${it.max}" }

    @TypeConverter
    fun toMaturityDays(value: String?): MaturityDays? = value?.split("-")?.let {
        if (it.size == 2) MaturityDays(it[0].toInt(), it[1].toInt()) else null
    }

    @TypeConverter
    fun fromGerminationSoilTemp(value: GerminationSoilTemp?): String? = value?.let { "${it.min}-${it.max}" }

    @TypeConverter
    fun toGerminationSoilTemp(value: String?): GerminationSoilTemp? = value?.split("-")?.let {
        if (it.size == 2) GerminationSoilTemp(it[0].toInt(), it[1].toInt()) else null
    }

    @TypeConverter
    fun fromMatureHeight(value: MatureHeight?): String? = value?.let { "${it.min}-${it.max}" }

    @TypeConverter
    fun toMatureHeight(value: String?): MatureHeight? = value?.split("-")?.let {
        if (it.size == 2) MatureHeight(it[0].toInt(), it[1].toInt()) else null
    }

    @TypeConverter
    fun fromMatureWidth(value: MatureWidth?): String? = value?.let { "${it.min}-${it.max}" }

    @TypeConverter
    fun toMatureWidth(value: String?): MatureWidth? = value?.split("-")?.let {
        if (it.size == 2) MatureWidth(it[0].toInt(), it[1].toInt()) else null
    }

    @TypeConverter
    fun fromRowSpacing(value: RowSpacing?): String? = value?.let { "${it.min}-${it.max}" }

    @TypeConverter
    fun toRowSpacing(value: String?): RowSpacing? = value?.split("-")?.let {
        if (it.size == 2) RowSpacing(it[0].toInt(), it[1].toInt()) else null
    }
}
