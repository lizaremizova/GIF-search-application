package com.lizaremizova.gifsearch.domain.repository

import androidx.paging.PagingData
import com.lizaremizova.gifsearch.domain.model.Gif
import kotlinx.coroutines.flow.Flow

interface GifRepository {

    fun search(query: String): Flow<PagingData<Gif>>

    suspend fun getGif(id: String): Result<Gif>
}
