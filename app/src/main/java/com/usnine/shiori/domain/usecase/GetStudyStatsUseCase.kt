package com.usnine.shiori.domain.usecase

import com.usnine.shiori.data.local.StreakDataStore
import com.usnine.shiori.data.local.dao.LearnedKanaDao
import com.usnine.shiori.data.local.dao.ReviewDao
import com.usnine.shiori.domain.model.StudyStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class GetStudyStatsUseCase @Inject constructor(
    private val learnedKanaDao: LearnedKanaDao,
    private val reviewDao: ReviewDao,
    private val streakDataStore: StreakDataStore,
) {
    operator fun invoke(): Flow<StudyStats> {
        val startOfDay = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return combine(
            learnedKanaDao.getAll(),
            reviewDao.countTodayReviews(startOfDay),
            streakDataStore.streakDays,
        ) { learnedKana, todayReviews, streakDays ->
            StudyStats(
                totalBookmarks   = learnedKana.size,
                todayReviewCount = todayReviews,
                streakDays       = streakDays,
            )
        }
    }
}
