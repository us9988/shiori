package com.usnine.shiori.data.local

import androidx.room.TypeConverter
import com.usnine.shiori.data.local.entity.PartOfSpeech
import com.usnine.shiori.data.local.entity.PhraseCategory
import com.usnine.shiori.data.local.entity.WordLevel

class Converters {

    @TypeConverter
    fun fromPhraseCategory(category: PhraseCategory): String = category.name

    @TypeConverter
    fun toPhraseCategory(name: String): PhraseCategory = PhraseCategory.valueOf(name)

    @TypeConverter
    fun fromWordLevel(level: WordLevel): String = level.name

    @TypeConverter
    fun toWordLevel(name: String): WordLevel = WordLevel.valueOf(name)

    @TypeConverter
    fun fromPartOfSpeech(pos: PartOfSpeech): String = pos.name

    @TypeConverter
    fun toPartOfSpeech(name: String): PartOfSpeech = PartOfSpeech.valueOf(name)
}
