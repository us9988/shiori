package com.usnine.shiori.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.usnine.shiori.data.local.dao.BookmarkDao
import com.usnine.shiori.data.local.dao.LearnedKanaDao
import com.usnine.shiori.data.local.dao.PhraseDao
import com.usnine.shiori.data.local.dao.ReviewDao
import com.usnine.shiori.data.local.dao.WordDao
import com.usnine.shiori.data.local.entity.BookmarkEntity
import com.usnine.shiori.data.local.entity.LearnedKanaEntity
import com.usnine.shiori.data.local.entity.PhraseEntity
import com.usnine.shiori.data.local.entity.ReviewEntity
import com.usnine.shiori.data.local.entity.WordEntity

@Database(
    entities = [
        BookmarkEntity::class,
        WordEntity::class,
        ReviewEntity::class,
        PhraseEntity::class,
        LearnedKanaEntity::class,
    ],
    version = 8,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class ShioriDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun wordDao(): WordDao
    abstract fun reviewDao(): ReviewDao
    abstract fun phraseDao(): PhraseDao
    abstract fun learnedKanaDao(): LearnedKanaDao
}
