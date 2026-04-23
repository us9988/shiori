package com.usnine.shiori.domain.repository

import com.usnine.shiori.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun getAll(): Flow<List<Bookmark>>
    fun getRecent(limit: Int = 5): Flow<List<Bookmark>>
    suspend fun save(bookmark: Bookmark): Long
}
