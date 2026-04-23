package com.usnine.shiori.data.repository

import com.usnine.shiori.data.local.dao.BookmarkDao
import com.usnine.shiori.data.local.entity.BookmarkEntity
import com.usnine.shiori.domain.model.Bookmark
import com.usnine.shiori.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val dao: BookmarkDao,
) : BookmarkRepository {

    override fun getAll(): Flow<List<Bookmark>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getRecent(limit: Int): Flow<List<Bookmark>> =
        dao.getRecent(limit).map { list -> list.map { it.toDomain() } }

    override suspend fun save(bookmark: Bookmark): Long =
        dao.insert(bookmark.toEntity())
}

private fun BookmarkEntity.toDomain() = Bookmark(
    id          = id,
    japaneseText = japaneseText,
    koreanText  = koreanText,
    source      = source,
    savedAt     = savedAt,
)

private fun Bookmark.toEntity() = BookmarkEntity(
    id          = id,
    japaneseText = japaneseText,
    koreanText  = koreanText,
    source      = source,
    savedAt     = savedAt,
)
