package com.compose_chat.ui

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.compose_chat.R
import kotlinx.coroutines.launch
import toFileConvert
import java.io.File

@Composable
fun Camera(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier,
    onImageCaptured: (File) -> Unit
) {
    val microPhonePermissionResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            // Permission Granted
        } else {
            // Permission Denied
        }

    }
    LaunchedEffect(key1 = true) {
        microPhonePermissionResultLauncher.launch(android.Manifest.permission.CAMERA)


    }

    var takingPicture by remember { mutableStateOf(false) }

    var imageBitmap: Bitmap? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }
    var processingImage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val lifeCycleOwner = LocalLifecycleOwner.current
    controller.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    Box(modifier) {

        if (imageBitmap == null) AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PreviewView(it).apply {
                    this.controller = controller
                    controller.bindToLifecycle(lifeCycleOwner)
                }
            })


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (takingPicture) CircularProgressIndicator() else CameraButton( // CameraButton is a composable function defined below
                modifier = Modifier,
                onClick = {
                    takingPicture = true

                    controller.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {

                            override fun onCaptureSuccess(image: ImageProxy) {
                                super.onCaptureSuccess(image)
                                takingPicture = false
                                scope.launch {
                                    val matrix = Matrix().apply {
                                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                                    }
                                    imageBitmap = Bitmap.createBitmap(
                                        image.toBitmap(),
                                        0, 0, image.width, image.height, matrix, true
                                    )
                                }


                            }

                            override fun onError(exception: ImageCaptureException) {
                                super.onError(exception)
                                takingPicture = false
                            }
                        }

                    )
                }
            )
        }
        if (imageBitmap != null) {
            ImagePreview(
                imageBitmap = imageBitmap!!,
                processingImage = processingImage,
                onImageConfirm = {
                    processingImage = true
                    scope.launch {
                        val file = imageBitmap!!.toFileConvert(context)
                        //write file to internal storage
                        val uuid = java.util.UUID.randomUUID().toString()
                        var fileExtension = file.extension
                        val newFilePathInInteralDirectory =
                            context.filesDir.absolutePath + "/$uuid.$fileExtension"
                        val newFile = File(newFilePathInInteralDirectory)
                        file.copyTo(newFile)


                        val exists = file.exists()
                        onImageCaptured(newFile)
                        processingImage = false
                        Log.d("🔥", "Camera: $exists")
                    }
                },
                onSuccess = {


                },
                onImageCancel = {
                    imageBitmap = null
                })
        }


    }
}

@Composable
fun CameraButton(
    modifier: Modifier,
    onClick: () -> Unit = {}
) {
    IconButton(
        modifier = modifier
            .padding(20.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(50)
            ),
        onClick = onClick
    ) {
        Image(
            painter = painterResource(id = R.drawable.camera_icon),
            contentDescription = "",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .size(60.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(5.dp)
                )
                .clip(shape = RoundedCornerShape(5.dp)),
        )
    }
}


@Composable
@Preview
fun CameraButtonPreview() {
    CameraButton(
        modifier = Modifier,
    )
}

