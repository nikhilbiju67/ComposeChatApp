package com.compose_chat.ui.components

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.compose_chat.R
import com.compose_chat.data.AndroidAudioRecorder
import com.compose_chat.domain.ChatUser
import com.compose_chat.domain.Message
import com.compose_chat.domain.MessageData
import com.compose_chat.domain.MessageStatus
import com.compose_chat.domain.MessageType
import com.compose_chat.ui.InputFieldStyle
import com.compose_chat.ui.defaultInputFieldStyle
import java.io.File
import java.time.LocalDateTime
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    modifier: Modifier,
    loggedInUser: ChatUser,
    recipient: ChatUser,
    onSendClick: (Message) -> Unit = {},
    inputFieldStyle: InputFieldStyle,
    onCameraAttachmentClick: () -> Unit = {}
) {
    0


    var showAttachmentSheet by remember { mutableStateOf(false) }
    var inputString by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()


    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        if (showAttachmentSheet) Dialog(

            onDismissRequest = {
                showAttachmentSheet = false
            }) {
            AttachmentSheet(
                modifier = Modifier.fillMaxWidth(),
                showAttachmentSheet = true,
                onAttachmentOutSideClick = {
                    showAttachmentSheet = false

                },
                onCameraAttachmentClick = {
                    onCameraAttachmentClick()
                },
                onImageAttachmentSelected = {
                    showAttachmentSheet = false

                    onSendClick(
                        Message(
                            author = loggedInUser,
                            recipient = recipient,
                            timestamp = LocalDateTime.now(),
                            messageData = MessageData(
                                file = File(it.path),
                                type = MessageType.IMAGE
                            ),
                            id = UUID.randomUUID().toString(),
                            status = MessageStatus.SENDING
                        )
                    )
                }
            )


        }
        InputRow(
            inputString = inputString,
            onInputChange = { inputString = it },
            onAttachmentClick = {
                showAttachmentSheet = true
//            scope.launch {
//                modelBottomSheet.show()
//            }
//                Log.d("Attachment", "Clicked")
            },
            onVoiceRecorded = {
                val file=it
               if(it!=null&&it.exists())
               {
                   onSendClick(
                       Message(
                           author = loggedInUser,
                           recipient = recipient,
                           timestamp = LocalDateTime.now(),
                           messageData = MessageData(file = it, type = MessageType.AUDIO),
                           id = UUID.randomUUID().toString(),
                           status = MessageStatus.SENDING

                       )
                   )
               }
            },
            onSendClick = {
                val uniqueId = UUID.randomUUID().toString()
                onSendClick(
                    Message(
                        author = loggedInUser,
                        recipient = recipient,
                        timestamp = LocalDateTime.now(),
                        messageData = MessageData(inputString, type = MessageType.TEXT),
                        id = uniqueId,
                        status = MessageStatus.SENDING
                    )
                )
                inputString = ""
            },
            inputFieldStyle = inputFieldStyle
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun InputRow(
    inputString: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onAttachmentClick: () -> Unit = {},
    onVoiceRecorded: (File?) -> Unit = {},
    inputFieldStyle: InputFieldStyle
) {


    var showRecordUi by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val context = LocalContext.current


        AnimatedContent(
            targetState = !showRecordUi,
            transitionSpec = {
                // Compare the incoming number with the previous number.
                if (targetState > initialState) {
                    // If the target number is larger, it slides up and fades in
                    // while the initial (smaller) number slides up and fades out.
                    (slideInVertically { height -> height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> -height } + fadeOut())
                } else {
                    // If the target number is smaller, it slides down and fades in
                    // while the initial number slides down and fades out.
                    (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> height } + fadeOut())
                }.using(
                    // Disable clipping since the faded slide-in/out should
                    // be displayed out of bounds.
                    SizeTransform(clip = false)
                )
            },
            modifier = Modifier.weight(1f), label = ""
        ) { targetExpanded ->
            if (targetExpanded) TextInput(
                onAttachmentClick = {

                    onAttachmentClick()
                },
                modifier = Modifier.weight(.4f),
                inputString = inputString,
                onInputChange = onInputChange,
                inputFieldStyle = inputFieldStyle
            )
            else {

                AudioInput()

            }


        }

        SendMessageButton(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(.2f),

            inputString = inputString,
            onRecordStarting = {
                showRecordUi = true


            },
            onRecordProgress = {

            },
            onRecordStop = {
                showRecordUi = false
                onVoiceRecorded(it)
                Log.d("file", it?.exists().toString())
            },
            onSendClick = onSendClick,
            inputFieldStyle = inputFieldStyle
        )
    }
}

@Composable
fun AttachmentButton(modifier: Modifier, onClick: () -> Unit) {
    IconButton(
        modifier = modifier
            .clip(CircleShape)
            .background(color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
        onClick = onClick
    ) {
        Image(
            imageVector = Icons.Filled.Add,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun AudioInput() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(20.dp)
            )
            .background(color = MaterialTheme.colorScheme.background)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(
                        alpha = 0.5f
                    )
                ),
            onClick = {}
        ) {
            BlinkView {
                Image(
                    painter = painterResource(id = R.drawable.baseline_mic_24),
                    modifier = Modifier.size(16.dp),
                    contentDescription = ""
                )
            }
        }
        Text(
            text = "Recording...",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun TextInput(
    modifier: Modifier,
    inputString: String,
    onInputChange: (String) -> Unit,
    onAttachmentClick: () -> Unit = {},
    inputFieldStyle: InputFieldStyle
) {
    Row(
        modifier = modifier
            .clip(
                RoundedCornerShape(20.dp)
            )
            .background(color = inputFieldStyle.backGroundColor),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AttachmentButton(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f),
            onClick = {
                onAttachmentClick()

            })
        OutlinedTextField(
            modifier = Modifier
                .weight(4f),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = inputFieldStyle.focusedIndicatorColor,
                unfocusedIndicatorColor = inputFieldStyle.unfocusedIndicatorColor,
                focusedContainerColor = inputFieldStyle.focusedContainerColor,
                unfocusedContainerColor = inputFieldStyle.unfocusedContainerColor,
            ),
            shape = inputFieldStyle.textFieldShape,

            value = inputString,
            onValueChange = onInputChange
        )
    }
}

@Composable
fun SendMessageButton(
    modifier: Modifier,
    inputString: String,
    onSendClick: () -> Unit,
    onRecordStarting: () -> Unit,
    onRecordStop: (File?) -> Unit,
    onRecordProgress: (Int) -> Unit,
    inputFieldStyle: InputFieldStyle
) {
    if (inputString.trim().isEmpty()) MicButton(
        modifier,
        onRecordStarting = onRecordStarting,
        onRecordStop = onRecordStop,
        onRecordProgress = onRecordProgress,
        backGroundColor = inputFieldStyle.micButtonBackGroundColor,
        iconColor = inputFieldStyle.micIconColor


    )
    else {
        SendButton(
            Modifier, onSendClick,
            backGroundColor = inputFieldStyle.sendButtonBackGroundColor,
            iconColor = inputFieldStyle.micIconColor
        )
    }


}

@Composable
fun SendButton(
    modifier: Modifier,
    onSendClick: () -> Unit,
    backGroundColor: Color,
    iconColor: Color
) {
    IconButton(
        onClick = onSendClick,

        modifier = modifier
            .then(Modifier.size(50.dp))
            .clip(CircleShape)
            .background(color = backGroundColor)
    ) {

        Icon(

            Icons.AutoMirrored.Filled.Send,
            tint = iconColor,
            modifier = Modifier.size(16.dp),
            contentDescription = "Send"
        )

    }
}

@Composable
fun MicButton(
    modifier: Modifier,
    onRecordStarting: () -> Unit,
    onRecordStop: (File?) -> Unit,
    backGroundColor: Color,
    onRecordProgress: (Int) -> Unit,
    iconColor: Color
) {
    val context = LocalContext.current
    val audioRecorder by lazy {
        AndroidAudioRecorder(context)
    }
    var audioFile: File? = null

    val microPhonePermissionResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {

            try {
                onRecordStarting()
                val messageId = UUID.randomUUID().toString()
                File(context.cacheDir, messageId + ".mp3").also {
                    audioRecorder.start(it)
                    audioFile = it
                }

                Log.d("Permission", "Granted")
            } catch (e: Exception) {
                Log.e("Permission", e.message.toString())

            }
        } else {
            Log.d("Permission", "Denied")
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()


    if (pressed) {
        microPhonePermissionResultLauncher.launch(android.Manifest.permission.RECORD_AUDIO)



        Log.d("pressed", "pressed")
        DisposableEffect(Unit) {
            onDispose {
                onRecordStop(audioFile)
                audioRecorder.stop()
                Log.d("pressed", "disposed")
            }

        }
    }
    Row(modifier = modifier) {
        IconButton(
            onClick = {},
            interactionSource = interactionSource,

            modifier = Modifier
                .clip(CircleShape)
                .background(color = backGroundColor)
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_mic_24),
                colorFilter = ColorFilter.tint(iconColor),
                modifier = Modifier.size(16.dp),
                contentDescription = ""
            )
        }
    }
}


@Composable
fun CloseButton(onCloseClick: () -> Unit) {
    IconButton(
        onClick = onCloseClick
    ) {
        Image(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.close),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            contentDescription = "Close"
        )
    }
}


@Composable
@Preview(showBackground = true, backgroundColor = 0xFF10FFFF)
fun PreviewInputField() {
    InputField(
        modifier = Modifier.fillMaxWidth(),
        inputFieldStyle = defaultInputFieldStyle,
        loggedInUser = ChatUser("1", "John", "https://randomuser.me/api/port"),
        recipient = ChatUser("2", "Jane", "https://randomuser.me/api/port")
    )
}


@Composable
@Preview
fun PreviewAudioInput() {
    AudioInput()
}

@Composable
@Preview
fun PreviewTextInput() {
    TextInput(
        modifier = Modifier.fillMaxWidth(),
        inputString = "Hello",
        onInputChange = {},
        inputFieldStyle = defaultInputFieldStyle
    )
}

@Composable
@Preview
fun PreviewSendMessageButton() {
    SendMessageButton(
        modifier = Modifier.fillMaxWidth(),
        inputString = "Hello",
        onSendClick = {},
        onRecordStarting = {},
        onRecordProgress = {},
        onRecordStop = {},
        inputFieldStyle = defaultInputFieldStyle
    )
}

