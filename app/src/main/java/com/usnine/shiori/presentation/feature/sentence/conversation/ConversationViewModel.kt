package com.usnine.shiori.presentation.feature.sentence.conversation

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.usnine.shiori.data.local.dao.PhraseDao
import com.usnine.shiori.data.local.entity.PhraseEntity
import com.usnine.shiori.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val phraseDao: PhraseDao,
    @ApplicationContext private val context: Context,
) : MviViewModel<ConversationContract.UiEvent, ConversationContract.UiState, ConversationContract.UiEffect>(
    initialState = ConversationContract.UiState()
) {
    private var allPhrases: List<PhraseUi> = emptyList()
    private val player: ExoPlayer = ExoPlayer.Builder(context).build().also { p ->
        p.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    setState { copy(playingId = null) }
                }
            }
        })
    }

    init {
        observePhrases()
    }

    override fun handleEvent(event: ConversationContract.UiEvent) {
        when (event) {
            is ConversationContract.UiEvent.FilterChanged -> onFilterChanged(event.filter)
            is ConversationContract.UiEvent.PlayTapped    -> onPlayTapped(event.phrase)
        }
    }

    private fun observePhrases() {
        phraseDao.getAll()
            .onEach { entities ->
                allPhrases = entities.map { it.toUi() }
                setState { copy(displayedPhrases = applyFilter(selectedFilter)) }
            }
            .launchIn(viewModelScope)
    }

    private fun onFilterChanged(filter: ConversationFilter) {
        player.stop()
        setState {
            copy(
                selectedFilter   = filter,
                displayedPhrases = applyFilter(filter),
                playingId        = null,
            )
        }
    }

    private fun onPlayTapped(phrase: PhraseUi) {
        if (state.value.playingId == phrase.id) {
            player.stop()
            setState { copy(playingId = null) }
            return
        }

        val uri = android.net.Uri.parse("asset:///audio/${phrase.audioFileName}")
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.play()
        setState { copy(playingId = phrase.id) }
    }

    private fun applyFilter(filter: ConversationFilter): List<PhraseUi> =
        if (filter.category == null) allPhrases
        else allPhrases.filter { it.category == filter.category }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}

private fun PhraseEntity.toUi() = PhraseUi(
    id            = id,
    japanese      = japanese,
    hiragana      = hiragana,
    korean        = korean,
    category      = category,
    level         = level,
    audioFileName = audioFileName,
)
