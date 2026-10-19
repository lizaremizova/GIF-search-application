package com.lizaremizova.gifsearch.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lizaremizova.gifsearch.data.remote.GiphyApi
import com.lizaremizova.gifsearch.data.remote.toDomain
import com.lizaremizova.gifsearch.domain.model.Gif
import com.lizaremizova.gifsearch.domain.repository.GifRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

internal class GifRepositoryImpl @Inject constructor(
    private val api: GiphyApi,
) : GifRepository {

    override fun search(query: String): Flow<PagingData<Gif>> = pager(query)

    override fun trending(): Flow<PagingData<Gif>> = pager(query = null)

    private fun pager(query: String?): Flow<PagingData<Gif>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { GifPagingSource(api, query) },
        ).flow

    override suspend fun getGif(id: String): Result<Gif> = try {
        api.getGif(id).data.toDomain()
            ?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("GIF has no images"))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    private companion object {
        const val PAGE_SIZE = 25
    }
}
