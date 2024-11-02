package com.compose_chat.ui.components

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import com.compose_chat.R
import java.io.File

@Composable
fun AttachmentSheet(
    modifier: Modifier,
    showAttachmentSheet: Boolean,
    onImageAttachmentSelected: (Uri) -> Unit = {},
    onAttachmentOutSideClick: () -> Unit,
    onCameraAttachmentClick: () -> Unit = {}
) {
    var showAttachmentSheetFlag by remember { mutableStateOf(showAttachmentSheet) }

    LaunchedEffect(showAttachmentSheet) {
        showAttachmentSheetFlag = true
    }

    if (showAttachmentSheetFlag) {
        Dialog(
            onDismissRequest = {
                showAttachmentSheetFlag = false
                onAttachmentOutSideClick()
            },

            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true, dismissOnClickOutside = true
            )
        ) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = modifier
                    .clickable {
                        onAttachmentOutSideClick()
                    }
                    .padding(vertical = 16.dp)

            ) {
                AttachmentOptions(
                    onConfirmImage = {
                        showAttachmentSheetFlag = false
                        onImageAttachmentSelected(it)
                        Log.d("🔥", "AttachmentSheet: $it")
                    }, onConfirmCamera = {
                        showAttachmentSheetFlag = false
                        onCameraAttachmentClick()
                    }

                )
            }

        }
    }
}

@Composable
fun AttachmentOptions(
    modifier: Modifier = Modifier,
    onConfirmImage: (Uri) -> Unit = {},
    onConfirmCamera: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val rememberShowImagePreview = remember { mutableStateOf(false) }
        var selectedImageUri: Uri by remember { mutableStateOf(Uri.EMPTY) }
        val context = androidx.compose.ui.platform.LocalContext.current
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                uri?.let { selectedUri ->
                    // Get content resolver
                    val contentResolver = context.contentResolver

                    // Get the file extension from the URI
                    val fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString())
                    val mimeType = contentResolver.getType(selectedUri)
                    val resolvedFileExtension = if (fileExtension.isNullOrEmpty()) {
                        MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                    } else {
                        fileExtension
                    }

                    // Get the actual file name from the URI
                    val cursor = contentResolver.query(selectedUri, null, null, null, null)
                    var fileName: String? = null
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (displayNameIndex != -1) {
                                fileName = it.getString(displayNameIndex)
                            }
                        }
                    }

                    // Create a file in internal storage with the same extension
                    fileName = fileName
                        ?: "file.${resolvedFileExtension}" // fallback if file name is not available
                    val fileOutputStream = context.openFileOutput(fileName!!, Context.MODE_PRIVATE)

                    // Open an input stream to read data from the URI
                    contentResolver.openInputStream(selectedUri)?.use { inputStream ->
                        // Read data from input stream and write to output stream
                        inputStream.copyTo(fileOutputStream)
                    }

                    // Close the file output stream
                    fileOutputStream.close()

                    // Now you can use the saved file path
                    val savedFilePath = File(context.filesDir, fileName!!).absolutePath

                    // Optionally, you can update your UI or perform any other necessary actions
                    rememberShowImagePreview.value = true
                    selectedImageUri = savedFilePath.toUri()

                    Log.d("🔥", "AttachmentOptions: $savedFilePath")
                }


            }


        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(color = MaterialTheme.colorScheme.background)
                .padding(vertical = 24.dp, horizontal = 24.dp), verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {


                Column(

                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        onConfirmCamera()

                    }
                ) {
                    Image(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(id = R.drawable.camera_icon),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                        contentDescription = "Camera"
                    )
                    Text("Camera", style = MaterialTheme.typography.bodySmall)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        launcher.launch(
                            PickVisualMediaRequest(
                                //Here we request only photos. Change this to .ImageAndVideo if
                                //you want videos too.
                                //Or use .VideoOnly if you only want videos.
                                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )

                    },
                ) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.gallery),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                        contentDescription = "Gallery"
                    )
                    Text("Gallery",style = MaterialTheme.typography.bodySmall)
                }
            }
        }




        if (rememberShowImagePreview.value) {
            SelectedImagePreview(
                onConfirmImage = {
                    rememberShowImagePreview.value = false
                    onConfirmImage(selectedImageUri)
                },
                onCancel = { rememberShowImagePreview.value = false },
                onDismissRequest = { rememberShowImagePreview.value = false },
                imageUri = selectedImageUri,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}


@Composable
@Preview(name = "AttachmentSheet")
private fun PreviewAttachmentSheet() {
    AttachmentSheet(
        modifier = Modifier,
        showAttachmentSheet = true,
        onAttachmentOutSideClick = {}
    )
}