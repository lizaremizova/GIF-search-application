package com.lizaremizova.gifsearch.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lizaremizova.gifsearch.data.remote.GiphyApi
import com.lizaremizova.gifsearch.domain.model.Gif
import com.lizaremizova.gifsearch.domain.repository.GifRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GifRepositoryImpl @Inject constructor(
    private val api: GiphyApi,
) : GifRepository {

    override fun search(query: String): Flow<PagingData<Gif>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { GifPagingSource(api, query) },
        ).flow

    override suspend fun getGif(id: String): Result<Gif> {
        // TODO: call api.getGif(id), map with toDomain(), return failure on errors / null mapping.
        TODO("Implement")
    }

    private companion object {
        const val PAGE_SIZE = 25
    }
}
