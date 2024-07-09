package com.compose_chat.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.commandiron.compose_loading.Circle
import com.compose_chat.R
import com.compose_chat.domain.AudioPlayer
import com.compose_chat.domain.Message
import com.compose_chat.domain.MessageData
import com.compose_chat.domain.MessageType
import com.compose_chat.ui.ChatBubbleStyle
import com.compose_chat.ui.ComposeChatStyle
import com.compose_chat.ui.defaultComposeChatStyle
import com.compose_chat.ui.incomingBubbleShape
import com.compose_chat.ui.outgoingBubbleShape
import com.compose_chat.utils.formatTime
import com.smarttoolfactory.bubble.BubbleShadow
import com.smarttoolfactory.bubble.bubble
import java.io.File
import java.time.LocalDateTime

fun copyToClipboard(context: Context, text: String) {
    val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("password", text)
    clipboardManager.setPrimaryClip(clip)
}

@Composable
fun MessageBubble(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    shadow: BubbleShadow? = null,
    borderStroke: BorderStroke? = null,
    message: Message,
    isSender: Boolean = false,
    audioPlayer: AudioPlayer,
    mediaProgress: Float = 0f,
    isAudioPlaying: Boolean = false,
    bubbleStyle: ChatBubbleStyle,
    composeChatStyle: ComposeChatStyle,
    imageBuilder: (@Composable (File?,String?) -> Unit)? = null,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Column(

            modifier
                .align(if (isSender) Alignment.BottomEnd else Alignment.BottomStart)
                .padding(
                    end = if (isSender) 0.dp else 25.dp,
                    start = if (isSender) 25.dp else 0.dp,
                )
                .bubble(
                    bubbleState = if (isSender) incomingBubbleShape else outgoingBubbleShape,

                    color = bubbleStyle.backGroundColor,
                    shadow = shadow,

                    borderStroke = borderStroke,

                    )
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalAlignment = if (isSender) Alignment.End else Alignment.Start,
        ) {

            when (message.messageData.type) {
                MessageType.TEXT -> {
                    message.messageData.message?.let {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = it,
                            color =bubbleStyle.messageTextStyle.color,
                            style = bubbleStyle.messageTextStyle,
                        )
                    }
                }

                MessageType.IMAGE -> {
                    val context = LocalContext.current
                    if(imageBuilder!=null){
                        imageBuilder.invoke(message.messageData.file,message.messageData.url)
                    }else if (message.messageData.file != null && message.messageData.file.exists()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(message.messageData.file.absolutePath)
                                .build(),
                            contentDescription = "icon",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .width(300.dp)
                        )
                    } else Column {
                        AsyncImage(
                            modifier = Modifier
                                .width(300.dp),
                            placeholder = debugPlaceholder(debugPreview = R.drawable.gallery),
                            model = message.messageData.url ?: "", contentDescription = "image",
                            contentScale = ContentScale.FillWidth
                        )
                    }
                }

                MessageType.VIDEO -> {
                    Text("Video")
                }

                MessageType.AUDIO -> {
                    AudioPlayerWidget(
                        modifier = Modifier.width(300.dp),
                        audioRemoteUrl = message.messageData.url ?: "",
                        audioPlayer = audioPlayer,
                        isAudioPlaying = isAudioPlaying,
                        mediaProgress = mediaProgress,
                        audioLocalFile = message.messageData.file


                    )
                }

                MessageType.UNKNOWN -> {
                    Text("Unknown")
                }

            }
            Column(horizontalAlignment = Alignment.End) {
                if (message.status == com.compose_chat.domain.MessageStatus.SENDING) {
                    Circle(

                        color = MaterialTheme.colorScheme.background,
                        durationMillis = 1000,
                         size = 10.dp,
                        circleSizeRatio = 0.5f


                    )
                }
                Text(
                    message.timestamp.formatTime(composeChatDateFormat = composeChatStyle.bubbleTimeFormater),
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(vertical = 4.dp),

                    color = bubbleStyle.timeTextStyle.color,
                    style = bubbleStyle.timeTextStyle
                )
            }
        }
    }


}

@Composable
@Preview
fun PreviewMessageBubble() {
    MessageBubble(


        message = Message(
            recipient = com.compose_chat.domain.ChatUser(
                id = "2",
                name = "Jane",
                imageUrl = "https://randomuser.me/api/port"
            ),
            messageData = MessageData("", type = MessageType.IMAGE),
            id = "",
            timestamp = LocalDateTime.now(),
            author = com.compose_chat.domain.ChatUser(
                id = "1",
                name = "John",
                imageUrl = "https://randomuser.me/api/port"
            )
        ),
        bubbleStyle = ChatBubbleStyle(
            userId = "1",
            backGroundColor = Color.Blue,
            textColor = Color.White,
            timeTextStyle = MaterialTheme.typography.bodySmall
        ),
        composeChatStyle = defaultComposeChatStyle,
        audioPlayer = object : AudioPlayer {
            override var playingResource: String? = null

            override fun playAudio(file: File?, url: String?) {
                println("🔊 playAudio: $url")
            }

            override fun stopAudio() {
                println("🔊 stopAudio")
            }

            override var audioProgress: Int
                get() = TODO("Not yet implemented")
                set(value) {}
        }
    )
}

@Composable
fun debugPlaceholder(@DrawableRes debugPreview: Int) =
    if (LocalInspectionMode.current) {
        painterResource(id = debugPreview)
    } else {
        null
    }


