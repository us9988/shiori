package com.usnine.shiori.presentation.feature.kana

import androidx.lifecycle.viewModelScope
import com.usnine.shiori.data.local.KanaData
import com.usnine.shiori.data.local.KanaRow
import com.usnine.shiori.data.local.dao.LearnedKanaDao
import com.usnine.shiori.data.local.entity.LearnedKanaEntity
import com.usnine.shiori.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KanaViewModel @Inject constructor(
    private val learnedKanaDao: LearnedKanaDao,
) : MviViewModel<KanaContract.UiEvent, KanaContract.UiState, KanaContract.UiEffect>(
    initialState = KanaContract.UiState()
) {
    init {
        observeLearnedKana()
        loadRows(KanaTab.HIRAGANA)
    }

    override fun handleEvent(event: KanaContract.UiEvent) {
        when (event) {
            is KanaContract.UiEvent.TabChanged        -> onTabChanged(event.tab)
            is KanaContract.UiEvent.KanaTapped        -> onKanaTapped(event.kana)
            is KanaContract.UiEvent.AllQuizCountChanged -> setState { copy(allQuizCount = event.count) }
        }
    }

    private fun observeLearnedKana() {
        learnedKanaDao.getAll()
            .onEach { entities ->
                setState { copy(learnedSet = entities.map { it.kana }.toSet()) }
            }
            .launchIn(viewModelScope)
    }

    private fun onTabChanged(tab: KanaTab) {
        setState { copy(selectedTab = tab) }
        loadRows(tab)
    }

    private fun onKanaTapped(kana: String) {
        viewModelScope.launch {
            if (kana in state.value.learnedSet) {
                learnedKanaDao.deleteByKana(kana)
            } else {
                learnedKanaDao.insert(LearnedKanaEntity(kana = kana))
            }
            // learnedSet은 Flow observer가 자동으로 갱신
        }
    }

    private fun loadRows(tab: KanaTab) {
        val rows: List<KanaRow> = when (tab) {
            KanaTab.HIRAGANA -> KanaData.hiraganaRows
            KanaTab.KATAKANA -> KanaData.katakanaRows
            KanaTab.DAKUTEN  -> KanaData.dakutenRows
            KanaTab.ALL      -> emptyList()
        }
        setState { copy(rows = rows) }
    }
}
