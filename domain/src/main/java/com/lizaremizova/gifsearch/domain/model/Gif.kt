package com.lizaremizova.gifsearch.domain.model

data class Gif(
    val id: String,
    val title: String,
    val username: String?,
    val pageUrl: String,
    /** Small animated rendition for the results grid. */
    val preview: GifImage,
    /** Full-size animated rendition for the details screen. */
    val original: GifImage,
)

data class GifImage(
    val url: String,
    val width: Int,
    val height: Int,
) {
    val aspectRatio: Float
        get() = if (width > 0 && height > 0) width.toFloat() / height else 1f
}
