package com.usnine.shiori.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

private val Context.streakDataStore: DataStore<Preferences> by preferencesDataStore(name = "streak")

@Singleton
class StreakDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val KEY_LAST_DATE  = stringPreferencesKey("last_study_date")
    private val KEY_STREAK     = intPreferencesKey("streak_days")
    private val KEY_IS_PREMIUM = booleanPreferencesKey("is_premium")

    val streakDays: Flow<Int> = context.streakDataStore.data
        .map { prefs -> prefs[KEY_STREAK] ?: 0 }

    val isPremium: Flow<Boolean> = context.streakDataStore.data
        .map { prefs -> prefs[KEY_IS_PREMIUM] ?: false }

    suspend fun setPremium(value: Boolean) {
        context.streakDataStore.edit { prefs ->
            prefs[KEY_IS_PREMIUM] = value
        }
    }

    suspend fun recordStudyToday() {
        val today     = LocalDate.now().toString()           // yyyy-MM-dd
        val yesterday = LocalDate.now().minusDays(1).toString()

        context.streakDataStore.edit { prefs ->
            val lastDate      = prefs[KEY_LAST_DATE]
            val currentStreak = prefs[KEY_STREAK] ?: 0

            val newStreak = when {
                lastDate == null      -> 1                   // 첫 사용
                lastDate == today     -> currentStreak       // 오늘 이미 기록
                lastDate == yesterday -> currentStreak + 1   // 연속
                else                  -> 1                   // 이틀 이상 끊김
            }

            prefs[KEY_STREAK] = newStreak
            if (lastDate != today) prefs[KEY_LAST_DATE] = today
        }
    }
}
