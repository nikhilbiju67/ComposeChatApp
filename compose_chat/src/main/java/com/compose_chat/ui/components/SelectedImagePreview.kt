package com.compose_chat.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.compose_chat.R


@Composable
fun SelectedImagePreview(
    modifier: Modifier,
    onDismissRequest: () -> Unit = {},
    imageUri: Uri,
    onConfirmImage: () -> Unit = {},
    onCancel: () -> Unit = {}
) {


    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false,

            ),


        onDismissRequest = {
            onDismissRequest()
        }) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colors.surface)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = imageUri
                ), contentDescription = "Selected Image",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
            //  close button
            IconButton(
                onClick = onDismissRequest,
                modifier = Modifier.align(Alignment.TopEnd)

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = "Close",
                    tint = MaterialTheme.colors.onSurface
                )
            }
            Actions(
                modifier = Modifier.align(Alignment.BottomCenter),
                onConfirmImage = onConfirmImage,
                onCancel = onCancel
            )

        }
    }

}

@Composable
private fun Actions(
    modifier: Modifier,
    onConfirmImage: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        IconButton(
            onClick = onConfirmImage,
            modifier = Modifier.weight(1f)


        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_check_24),
                    contentDescription = "Confirm",
                    tint = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(text = "Confirm", color = MaterialTheme.colors.onSurface)
            }
        }
        IconButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f)


        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = "Cancel",
                    tint = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(text = "Cancel", color = MaterialTheme.colors.onSurface)
            }
        }

    }
}


@Preview(name = "SelectedImagePreview", showBackground = true)
@Composable
private fun PreviewSelectedImagePreview() {
    SelectedImagePreview(
        onDismissRequest = {},
        imageUri = Uri.EMPTY,
        modifier = Modifier.fillMaxSize(),
        onConfirmImage = {},
        onCancel = {}

    )
}