package com.compose_chat.ui.components

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

@Composable
fun SafeImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    onImageSafe: (Bitmap) -> Boolean,

    ) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var isImageSafe by remember { mutableStateOf<Boolean?>(null) }
    val context = LocalContext.current

    LaunchedEffect(imageUrl) {
        val loader = ImageLoader(context = context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false)
            .build()

        val result = (loader.execute(request) as? SuccessResult)?.drawable
        val bitmap = (result as? BitmapDrawable)?.bitmap

        bitmap?.let {
            isImageSafe = !onImageSafe(it)
            if (isImageSafe == true) {
                imageBitmap = it.asImageBitmap()
            }
        }
    }

    when {
        isImageSafe == null -> {
            // Loading
            Text("Loading Image")

        }

        isImageSafe == true -> {
            imageBitmap?.let {
                Image(
                    bitmap = it,
                    contentDescription = "Loaded Image",
                    modifier = modifier
                )
            }
        }

        isImageSafe == false -> {
            Box {
                imageBitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "Loaded Image",
                        modifier = modifier
                    )
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("Image is not safe")
                }

            }
        }
    }
}

@Preview(name = "SafeImage")
@Composable
private fun PreviewSafeImage() {
    SafeImage(
        imageUrl = "https://www.example.com/image.jpg",
        onImageSafe = { true },

        )
}