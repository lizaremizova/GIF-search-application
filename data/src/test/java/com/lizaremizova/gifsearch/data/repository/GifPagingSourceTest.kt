package com.lizaremizova.gifsearch.data.repository

import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import com.lizaremizova.gifsearch.data.remote.GiphyApi
import com.lizaremizova.gifsearch.data.remote.dto.GifDto
import com.lizaremizova.gifsearch.data.remote.dto.ImageDto
import com.lizaremizova.gifsearch.data.remote.dto.ImagesDto
import com.lizaremizova.gifsearch.data.remote.dto.PaginationDto
import com.lizaremizova.gifsearch.data.remote.dto.SearchResponseDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class GifPagingSourceTest {

    private val api = mockk<GiphyApi>()
    private val source = GifPagingSource(api, "cats")

    private fun gif(id: String) = GifDto(id = id, images = ImagesDto(original = ImageDto(url = "https://x/$id.gif")))

    private suspend fun load(key: Int?) =
        source.load(LoadParams.Refresh(key = key, loadSize = 2, placeholdersEnabled = false))

    @Test
    fun `first page has next offset and drops gifs without images`() = runTest {
        coEvery { api.search("cats", 2, 0) } returns SearchResponseDto(
            data = listOf(gif("1"), GifDto(id = "no-images")),
            pagination = PaginationDto(totalCount = 10),
        )

        val page = load(key = null) as LoadResult.Page

        assertEquals(listOf("1"), page.data.map { it.id })
        assertNull(page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun `last page has no next key`() = runTest {
        coEvery { api.search("cats", 2, 8) } returns SearchResponseDto(
            data = listOf(gif("9"), gif("10")),
            pagination = PaginationDto(totalCount = 10),
        )

        val page = load(key = 8) as LoadResult.Page

        assertNull(page.nextKey)
    }

    @Test
    fun `stops at giphy offset limit`() = runTest {
        coEvery { api.search("cats", 2, 4998) } returns SearchResponseDto(
            data = listOf(gif("a"), gif("b")),
            pagination = PaginationDto(totalCount = 100_000),
        )

        val page = load(key = 4998) as LoadResult.Page

        assertNull(page.nextKey)
    }

    @Test
    fun `null query loads trending`() = runTest {
        coEvery { api.trending(2, 0) } returns SearchResponseDto(
            data = listOf(gif("t")),
            pagination = PaginationDto(totalCount = 10),
        )

        val page = GifPagingSource(api, query = null)
            .load(LoadParams.Refresh(key = null, loadSize = 2, placeholdersEnabled = false)) as LoadResult.Page

        assertEquals(listOf("t"), page.data.map { it.id })
    }

    @Test
    fun `network failure becomes error result`() = runTest {
        coEvery { api.search(any(), any(), any()) } throws IOException("offline")

        assertTrue(load(key = null) is LoadResult.Error)
    }
}
