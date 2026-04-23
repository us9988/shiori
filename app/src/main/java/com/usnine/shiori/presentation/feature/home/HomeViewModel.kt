package com.usnine.shiori.presentation.feature.home

import androidx.lifecycle.viewModelScope
import com.usnine.shiori.data.local.StreakDataStore
import com.usnine.shiori.domain.usecase.GetRecentBookmarksUseCase
import com.usnine.shiori.domain.usecase.GetTodayMissionUseCase
import com.usnine.shiori.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val streakDataStore: StreakDataStore,
    private val getTodayMissionUseCase: GetTodayMissionUseCase,
    private val getRecentBookmarksUseCase: GetRecentBookmarksUseCase,
) : MviViewModel<HomeContract.UiEvent, HomeContract.UiState, HomeContract.UiEffect>(
    initialState = HomeContract.UiState()
) {
    init {
        recordAndObserveStreak()
        observeBookmarks()
        loadMission()
    }

    override fun handleEvent(event: HomeContract.UiEvent) {
        when (event) {
            HomeContract.UiEvent.LoadData -> loadMission()
        }
    }

    private fun recordAndObserveStreak() {
        viewModelScope.launch { streakDataStore.recordStudyToday() }

        streakDataStore.streakDays
            .onEach { days -> setState { copy(streak = days) } }
            .launchIn(viewModelScope)
    }

    private fun observeBookmarks() {
        getRecentBookmarksUseCase()
            .onEach { bookmarks -> setState { copy(bookmarks = bookmarks) } }
            .launchIn(viewModelScope)
    }

    private fun loadMission() {
        viewModelScope.launch {
            val mission = getTodayMissionUseCase()
            setState { copy(todayMission = mission) }
        }
    }
}
