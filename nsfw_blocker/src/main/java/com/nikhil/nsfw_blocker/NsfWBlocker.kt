package com.nikhil.nsfw_blocker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.nikhil.nsfw_blocker.NsfWBlocker.isNSFW
import com.valentinilk.shimmer.shimmer
import io.github.devzwy.nsfw.NSFWHelper

object NsfWBlocker {
    private const val CONFIDENCE_THRESHOLD: Float = 0.7F

    fun initNSFW(applicationContext: Context) {
        NSFWHelper.openDebugLog()
        NSFWHelper.initHelper(
            context = applicationContext,
            isOpenGPU = false,
        )

    }
    fun isNSFW(
        bitmap: Bitmap?,
        confidenceThreshold: Float = CONFIDENCE_THRESHOLD,
    ): Boolean {
        if (bitmap == null) {
            return false
        }
        val score = NSFWHelper.getNSFWScore(bitmap).nsfwScore
        return score > confidenceThreshold

    }

}

@Composable
fun SafeImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    loadingView: @Composable () -> Unit = { ImageLoader() },
    unSafeView: @Composable (SafeImageData) -> Unit = { UnsafeView(imageUrl, false) },
    safeView: @Composable (SafeImageData) -> Unit = { SafeView(it, modifier) }
) {
    /// Mutable state to hold the image data
    var imageBitmap by remember { mutableStateOf<SafeImageData>(SafeImageData(imageUrl = "")) }
    /// Mutable state to track image loading status
    var imageLoading by remember { mutableStateOf(false) }
    /// Mutable state to track if the image is safe
    var isImageSafe by remember { mutableStateOf(false) }
    /// Retrieve the current context
    val context = LocalContext.current

    /// Function to check the safety of the image
    suspend fun setSafety() {
        imageUrl?.let {
            /// Start loading the image
            imageLoading = true
            /// Create an image loader with the context
            val loader = ImageLoader(context = context)
            /// Build an image request
            val request = ImageRequest.Builder(context)
                .data(it)
                .allowHardware(false)
                .build()
            /// Execute the request and get the result
            val result = (loader.execute(request) as? SuccessResult)?.drawable
            /// Extract the bitmap from the result
            val bitmap = (result as? BitmapDrawable)?.bitmap
            /// Check if the bitmap is safe
            val isSafe = bitmap?.let { !isNSFW(it) } ?: false
            /// Store the image data in the map
            safeImageMap[it] = SafeImageData(
                imageUrl = it,
                bitmap = bitmap,
                isSafe = isSafe
            )
            /// Update the state with the image data
            imageBitmap = safeImageMap[it]!!
            /// Mark loading as complete
            imageLoading = false
            /// Update the safety status
            isImageSafe = isSafe
        } ?: run {
            /// If imageUrl is null, retrieve the image data from the map
            imageBitmap = safeImageMap[imageUrl]!!
            /// Mark loading as complete
            imageLoading = false
            /// Update the safety status
            isImageSafe = safeImageMap[imageUrl]!!.isSafe
        }
    }

    /// Effect to check and set image safety when imageUrl changes
    LaunchedEffect(imageUrl) {
        if (imageUrl != null && !safeImageMap.containsKey(imageUrl)) {
            setSafety()
        } else if (imageUrl != null) {
            imageBitmap = safeImageMap[imageUrl]!!
            isImageSafe = imageBitmap.isSafe
        }
    }

    /// Display different views based on the loading and safety status
    when {
        /// Display loading view if the image is loading
        imageLoading -> {
            loadingView()
        }

        /// Display safe view if the image is safe
        isImageSafe -> {
            safeView(imageBitmap)
        }

        /// Display unsafe view if the image is not safe
        else -> {
            unSafeView(imageBitmap)
        }
    }
}

/// Composable function to display a safe image
@Composable
private fun SafeView(
    imageBitmap: SafeImageData,
    modifier: Modifier
) {
    /// Display the image using the bitmap if available
    imageBitmap.bitmap?.asImageBitmap()?.let {
        Image(
            bitmap = it,
            contentDescription = "Image",
            contentScale = ContentScale.FillWidth,
            modifier = modifier.fillMaxSize()
        )
    }
}

/// Composable function to display an unsafe image view
@Composable
private fun UnsafeView(imageUrl: String?, isImageSafe: Boolean) {
    var isImageSafe1 = isImageSafe
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(380.dp)
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .clickable {
                /// Mark the image as safe and update the state when clicked
                if (imageUrl != null) {
                    safeImageMap[imageUrl] =
                        safeImageMap[imageUrl]!!.copy(loading = true, isSafe = true)
                    isImageSafe1 = true
                }
            }
    ) {
        /// Display warning icon and text for unsafe image
        Column(verticalArrangement = Arrangement.Center) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Unsafe Image",
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                "Unsafe Image",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/// Composable function to display a loading view
@Composable
private fun ImageLoader() {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(180.dp)
            .background(MaterialTheme.colorScheme.primary)
            .shimmer()
    ) {

    }
}

/// Data class to hold the image data
data class SafeImageData(
    val imageUrl: String,
    val bitmap: Bitmap? = null,
    val isSafe: Boolean = false,
    val loading: Boolean = false
)

/// Map to store the safe image data
var safeImageMap: MutableMap<String, SafeImageData> = mutableMapOf()


@Preview(name = "SafeImage")
@Composable
private fun PreviewSafeImage() {
    SafeImage(
        imageUrl = "https://www.example.com/image.jpg",
    )

}