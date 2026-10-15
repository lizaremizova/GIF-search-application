package com.lizaremizova.gifsearch.data.remote

import com.lizaremizova.gifsearch.data.remote.dto.GifDto
import com.lizaremizova.gifsearch.data.remote.dto.ImageDto
import com.lizaremizova.gifsearch.domain.model.Gif
import com.lizaremizova.gifsearch.domain.model.GifImage

/** Returns null when the GIF has no usable animated rendition, so it can be skipped. */
internal fun GifDto.toDomain(): Gif? {
    val original = images?.original?.toDomain() ?: return null
    val preview = images?.fixedWidth?.toDomain() ?: original
    return Gif(
        id = id,
        title = title.orEmpty(),
        username = username?.takeIf { it.isNotBlank() },
        pageUrl = url.orEmpty(),
        preview = preview,
        original = original,
    )
}

private fun ImageDto.toDomain(): GifImage? {
    val url = url?.takeIf { it.isNotBlank() } ?: return null
    return GifImage(
        url = url,
        width = width?.toIntOrNull() ?: 0,
        height = height?.toIntOrNull() ?: 0,
    )
}
