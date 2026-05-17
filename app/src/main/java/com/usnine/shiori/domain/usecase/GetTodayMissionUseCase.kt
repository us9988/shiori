package com.usnine.shiori.domain.usecase

import android.content.Context
import com.usnine.shiori.R
import com.usnine.shiori.data.local.dao.WordProgressDao
import com.usnine.shiori.data.local.entity.WordLevel
import com.usnine.shiori.presentation.feature.home.MissionDestination
import com.usnine.shiori.presentation.feature.home.TodayMission
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetTodayMissionUseCase @Inject constructor(
    private val wordProgressDao: WordProgressDao,
    @ApplicationContext private val context: Context,
) {
    suspend operator fun invoke(): TodayMission {
        val n5Completed = wordProgressDao.countCompletedForLevel(WordLevel.N5.name)
        val label = if (n5Completed > 0)
            context.getString(R.string.mission_word_study_resume)
        else
            context.getString(R.string.mission_word_study_start)
        return TodayMission(label = label, destination = MissionDestination.WORD_STUDY_SCREEN)
    }
}
