package com.usnine.shiori.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.usnine.shiori.data.local.dao.BookmarkDao
import com.usnine.shiori.data.local.dao.LearnedKanaDao
import com.usnine.shiori.data.local.dao.PhraseDao
import com.usnine.shiori.data.local.dao.ReviewDao
import com.usnine.shiori.data.local.dao.WordDao
import com.usnine.shiori.data.local.dao.WordProgressDao
import com.usnine.shiori.data.local.entity.BookmarkEntity
import com.usnine.shiori.data.local.entity.LearnedKanaEntity
import com.usnine.shiori.data.local.entity.PhraseEntity
import com.usnine.shiori.data.local.entity.ReviewEntity
import com.usnine.shiori.data.local.entity.WordEntity
import com.usnine.shiori.data.local.entity.WordProgressEntity

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS word_progress (
                wordId INTEGER NOT NULL PRIMARY KEY,
                level TEXT NOT NULL,
                isCompleted INTEGER NOT NULL DEFAULT 0,
                completedAt INTEGER
            )
        """)
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE word_progress ADD COLUMN reviewCount INTEGER NOT NULL DEFAULT 0")
    }
}

@Database(
    entities = [
        BookmarkEntity::class,
        WordEntity::class,
        ReviewEntity::class,
        PhraseEntity::class,
        LearnedKanaEntity::class,
        WordProgressEntity::class,
    ],
    version = 10,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class ShioriDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun wordDao(): WordDao
    abstract fun reviewDao(): ReviewDao
    abstract fun phraseDao(): PhraseDao
    abstract fun learnedKanaDao(): LearnedKanaDao
    abstract fun wordProgressDao(): WordProgressDao
}
