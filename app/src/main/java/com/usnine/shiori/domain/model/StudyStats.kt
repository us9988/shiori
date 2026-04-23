package com.usnine.shiori.domain.model

data class StudyStats(
    val totalBookmarks: Int,
    val completedWords: Int,
    val todayReviewCount: Int,
    val streakDays: Int,
)
