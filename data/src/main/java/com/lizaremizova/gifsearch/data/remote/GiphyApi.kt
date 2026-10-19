package com.lizaremizova.gifsearch.data.remote

import com.lizaremizova.gifsearch.data.remote.dto.GifResponseDto
import com.lizaremizova.gifsearch.data.remote.dto.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** The api_key query parameter is added by an OkHttp interceptor in :core:network. */
interface GiphyApi {

    @GET("gifs/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("rating") rating: String = "g",
    ): SearchResponseDto

    @GET("gifs/trending")
    suspend fun trending(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("rating") rating: String = "g",
    ): SearchResponseDto

    @GET("gifs/{id}")
    suspend fun getGif(@Path("id") id: String): GifResponseDto
}
