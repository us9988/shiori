package com.usnine.shiori.domain.usecase

import com.usnine.shiori.domain.repository.WordRepository
import javax.inject.Inject

class ToggleWordBookmarkUseCase @Inject constructor(
    private val wordRepository: WordRepository,
) {
    suspend operator fun invoke(wordId: Long) = wordRepository.toggleBookmark(wordId)
}
