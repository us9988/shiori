package com.usnine.shiori.domain.usecase

import com.usnine.shiori.data.local.entity.PartOfSpeech
import com.usnine.shiori.domain.repository.WordRepository
import javax.inject.Inject

class GetWordsByPartOfSpeechUseCase @Inject constructor(
    private val wordRepository: WordRepository,
) {
    operator fun invoke(pos: PartOfSpeech) = wordRepository.getByPartOfSpeech(pos)
}
