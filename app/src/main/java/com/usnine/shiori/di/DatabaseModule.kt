package com.usnine.shiori.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.usnine.shiori.data.local.ShioriDatabase
import com.usnine.shiori.data.local.DataVersionDataStore
import com.usnine.shiori.data.local.PHRASE_VERSION
import com.usnine.shiori.data.local.WORD_VERSION
import com.usnine.shiori.data.local.allPhrases
import com.usnine.shiori.data.local.allWords
import kotlinx.coroutines.flow.first
import com.usnine.shiori.data.local.dao.BookmarkDao
import com.usnine.shiori.data.local.dao.LearnedKanaDao
import com.usnine.shiori.data.local.dao.PhraseDao
import com.usnine.shiori.data.local.dao.ReviewDao
import com.usnine.shiori.data.local.dao.WordDao
import com.usnine.shiori.data.repository.BookmarkRepositoryImpl
import com.usnine.shiori.data.repository.WordRepositoryImpl
import com.usnine.shiori.domain.repository.BookmarkRepository
import com.usnine.shiori.domain.repository.WordRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

// level 컬럼은 "N5" 등 이미 enum name과 동일하지만, partOfSpeech는 한글 값이라
// 기존 words 행을 모두 삭제하고 enum 기반 저장으로 전환한다.
private val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DELETE FROM words")
    }
}

private val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE words ADD COLUMN isBookmarked INTEGER NOT NULL DEFAULT 0")
    }
}

private val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE words ADD COLUMN koreanReading TEXT NOT NULL DEFAULT \"\"")
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideShioriDatabase(
        @ApplicationContext context: Context,
        dataVersionDataStore: DataVersionDataStore,
    ): ShioriDatabase {
        lateinit var instance: ShioriDatabase
        instance = Room.databaseBuilder(
            context,
            ShioriDatabase::class.java,
            "shiori.db",
        )
            .addMigrations(MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        instance.wordDao().insertAll(allWords)
                    }
                }
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        val savedWordVersion = dataVersionDataStore.wordDataVersion.first()
                        if (savedWordVersion < WORD_VERSION) {
                            val currentCount = instance.wordDao().getCount()
                            val newWords = allWords.filter { it.id > currentCount }
                            if (newWords.isNotEmpty()) {
                                instance.wordDao().insertAll(newWords)
                            }
                            dataVersionDataStore.setWordDataVersion(WORD_VERSION)
                        }

                        val savedPhraseVersion = dataVersionDataStore.phraseDataVersion.first()
                        if (savedPhraseVersion < PHRASE_VERSION) {
                            val currentCount = instance.phraseDao().getCount()
                            val newPhrases = allPhrases.filter { it.id > currentCount }
                            if (newPhrases.isNotEmpty()) {
                                instance.phraseDao().insertAll(newPhrases)
                            }
                            dataVersionDataStore.setPhraseDataVersion(PHRASE_VERSION)
                        }
                    }
                }
            })
            .build()
        return instance
    }

    @Provides
    @Singleton
    fun provideBookmarkDao(db: ShioriDatabase): BookmarkDao = db.bookmarkDao()

    @Provides
    @Singleton
    fun provideWordDao(db: ShioriDatabase): WordDao = db.wordDao()

    @Provides
    @Singleton
    fun provideReviewDao(db: ShioriDatabase): ReviewDao = db.reviewDao()

    @Provides
    @Singleton
    fun providePhraseDao(db: ShioriDatabase): PhraseDao = db.phraseDao()

    @Provides
    @Singleton
    fun provideLearnedKanaDao(db: ShioriDatabase): LearnedKanaDao = db.learnedKanaDao()

}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(impl: BookmarkRepositoryImpl): BookmarkRepository

    @Binds
    @Singleton
    abstract fun bindWordRepository(impl: WordRepositoryImpl): WordRepository
}
