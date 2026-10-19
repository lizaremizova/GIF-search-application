package com.lizaremizova.gifsearch.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lizaremizova.gifsearch.data.remote.GiphyApi
import com.lizaremizova.gifsearch.data.remote.toDomain
import com.lizaremizova.gifsearch.domain.model.Gif
import kotlin.coroutines.cancellation.CancellationException

/** Giphy paginates by offset, so the paging key is the offset of the first item in a page. */
internal class GifPagingSource(
    private val api: GiphyApi,
    private val query: String,
) : PagingSource<Int, Gif>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Gif> {
        val offset = params.key ?: 0
        return try {
            val response = api.search(query, params.loadSize, offset)
            val nextOffset = offset + response.data.size
            // Giphy rejects offsets above 4999, even when total_count is larger.
            val total = minOf(response.pagination?.totalCount ?: 0, MAX_OFFSET + 1)
            LoadResult.Page(
                data = response.data.mapNotNull { it.toDomain() },
                prevKey = null, // pages are only appended; refresh always starts from the top
                nextKey = if (response.data.isEmpty() || nextOffset >= total) null else nextOffset,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Gif>): Int? = null

    private companion object {
        const val MAX_OFFSET = 4999
    }
}
