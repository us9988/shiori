package com.usnine.shiori.domain.usecase

import com.usnine.shiori.domain.model.Bookmark
import com.usnine.shiori.domain.repository.BookmarkRepository
import javax.inject.Inject

class SaveBookmarkUseCase @Inject constructor(
    private val repository: BookmarkRepository,
) {
    suspend operator fun invoke(bookmark: Bookmark): Long = repository.save(bookmark)
}
