package com.usnine.shiori.domain.usecase

import com.usnine.shiori.domain.model.Bookmark
import com.usnine.shiori.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarksUseCase @Inject constructor(
    private val repository: BookmarkRepository,
) {
    operator fun invoke(): Flow<List<Bookmark>> = repository.getAll()
}
