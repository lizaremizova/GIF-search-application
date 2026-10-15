package com.lizaremizova.gifsearch.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lizaremizova.gifsearch.data.remote.GiphyApi
import com.lizaremizova.gifsearch.domain.model.Gif

/** Giphy paginates by offset, so the paging key is the offset of the first item in a page. */
internal class GifPagingSource(
    private val api: GiphyApi,
    private val query: String,
) : PagingSource<Int, Gif>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Gif> {
        // TODO:
        //  1. offset = params.key ?: 0
        //  2. call api.search(query, params.loadSize, offset)
        //  3. map items with toDomain(), dropping nulls
        //  4. prevKey = null on the first page, nextKey = null when the end is reached
        //     (empty page, or offset + count >= pagination.totalCount)
        //  5. wrap IOException / HttpException in LoadResult.Error (don't catch CancellationException)
        TODO("Implement pagination")
    }

    override fun getRefreshKey(state: PagingState<Int, Gif>): Int? {
        // TODO: return an offset near state.anchorPosition so refresh keeps the scroll position.
        return null
    }
}
