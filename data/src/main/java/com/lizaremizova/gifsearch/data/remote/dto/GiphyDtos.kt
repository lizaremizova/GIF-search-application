package com.lizaremizova.gifsearch.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Only the fields the app uses are declared; Json is configured with ignoreUnknownKeys.
// See https://developers.giphy.com/docs/api/schema

@Serializable
data class SearchResponseDto(
    val data: List<GifDto> = emptyList(),
    val pagination: PaginationDto? = null,
)

@Serializable
data class GifResponseDto(
    val data: GifDto,
)

@Serializable
data class PaginationDto(
    @SerialName("total_count") val totalCount: Int = 0,
    val count: Int = 0,
    val offset: Int = 0,
)

@Serializable
data class GifDto(
    val id: String,
    val title: String? = null,
    val username: String? = null,
    val url: String? = null,
    val images: ImagesDto? = null,
)

@Serializable
data class ImagesDto(
    @SerialName("fixed_width") val fixedWidth: ImageDto? = null,
    val original: ImageDto? = null,
)

/** Giphy returns sizes as strings, e.g. "200". */
@Serializable
data class ImageDto(
    val url: String? = null,
    val width: String? = null,
    val height: String? = null,
)
