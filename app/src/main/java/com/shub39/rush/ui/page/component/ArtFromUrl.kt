package com.shub39.rush.ui.page.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.shub39.rush.R
import org.koin.compose.koinInject

@Composable
fun ArtFromUrl(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    placeholder: Int = R.drawable.round_music_note_24,
    imageLoader: ImageLoader = koinInject()
) {
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .apply {
                    placeholder(placeholder)
                    error(R.drawable.baseline_landscape_24)
                    crossfade(true)
                }
                .build(),
            imageLoader = imageLoader
        ),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}