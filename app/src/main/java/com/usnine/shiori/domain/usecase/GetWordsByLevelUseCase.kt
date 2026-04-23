package com.usnine.shiori.domain.usecase

import com.usnine.shiori.data.local.entity.WordLevel
import com.usnine.shiori.domain.repository.WordRepository
import javax.inject.Inject

class GetWordsByLevelUseCase @Inject constructor(
    private val wordRepository: WordRepository,
) {
    operator fun invoke(level: WordLevel) = wordRepository.getByLevel(level)
}
