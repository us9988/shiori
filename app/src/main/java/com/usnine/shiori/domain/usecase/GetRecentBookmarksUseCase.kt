package com.usnine.shiori.domain.usecase

import com.usnine.shiori.data.local.dao.BookmarkDao
import com.usnine.shiori.presentation.feature.home.BookmarkUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRecentBookmarksUseCase @Inject constructor(
    private val bookmarkDao: BookmarkDao,
) {
    operator fun invoke(limit: Int = 3): Flow<List<BookmarkUi>> =
        bookmarkDao.getRecent(limit).map { list ->
            list.map { entity ->
                BookmarkUi(
                    id     = entity.id,
                    jpText = entity.japaneseText,
                    krText = entity.koreanText,
                    source = entity.source,
                )
            }
        }
}
