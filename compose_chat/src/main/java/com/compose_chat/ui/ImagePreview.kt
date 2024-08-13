package com.compose_chat.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


@Composable
fun ImagePreview(
    modifier: Modifier = Modifier,
    imageBitmap: Bitmap,
    processingImage: Boolean,
    onImageConfirm: (Bitmap) -> Unit = {},
    onSuccess: () -> Unit = {},
    onImageCancel: () -> Unit = {}
) {
    InitialScreen(modifier, imageBitmap, onImageConfirm, onImageCancel, processingImage)
}

@Composable
private fun InitialScreen(
    modifier: Modifier,
    imageBitmap: Bitmap,
    onImageConfirm: (Bitmap) -> Unit,
    onImageCancel: () -> Unit = {},
    processingImage: Boolean
) {
    var processing by remember {
        mutableStateOf(false)
    }
    if (processing) Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()

    }
    else Box(modifier.background(color = MaterialTheme.colorScheme.background)) {

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(),
            painter = rememberAsyncImagePainter(imageBitmap),
            contentDescription = null,

            contentScale = ContentScale.FillWidth
        )
        if (processingImage) LoadingButton(modifier = Modifier) else Row(
            modifier = Modifier.align(
                alignment = androidx.compose.ui.Alignment.BottomCenter
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CancelButton(modifier = Modifier, onClick = {
                onImageCancel()
            })
            Confirmutton(

                modifier =
                Modifier,
                onClick = {
                    processing = true
                    onImageConfirm(imageBitmap)
                }

            )

        }
    }
}

@Composable
fun Confirmutton(
    modifier: Modifier,
    onClick: () -> Unit = {}
) {
    IconButton(
        modifier = modifier
            .padding(20.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(50)
            )
            .padding(20.dp),
        onClick = onClick
    ) {
        Image(
            painter = painterResource(id = com.compose_chat.R.drawable.baseline_check_24),
            contentDescription = "",
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondaryContainer),
            modifier = Modifier
                .size(60.dp),
        )
    }
}

@Composable
fun CancelButton(
    modifier: Modifier,
    onClick: () -> Unit = {}
) {
    IconButton(
        modifier = modifier
            .padding(10.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(50)
            )
            .padding(10.dp),
        onClick = onClick
    ) {
        Image(
            painter = painterResource(id = com.compose_chat.R.drawable.close),
            contentDescription = "",
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondaryContainer),
            modifier = Modifier
                .size(40.dp),
        )
    }
}

@Composable
fun LoadingButton(
    modifier: Modifier,
    onClick: () -> Unit = {}
) {
    IconButton(
        modifier = modifier
            .padding(10.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(50)
            )
            .padding(10.dp),
        onClick = onClick
    ) {
        CircularProgressIndicator()
    }
}


@Preview(name = "ImagePreview", showBackground = true)
@Composable
private fun PreviewImagePreview() {
    InitialScreen(
        modifier = Modifier,
        imageBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888),
        onImageConfirm = {}, processingImage = false
    )

}


@Preview(name = "Confirmutton")
@Composable
private fun PreviewConfirmutton() {
    Confirmutton(
        modifier = Modifier,
    )
}