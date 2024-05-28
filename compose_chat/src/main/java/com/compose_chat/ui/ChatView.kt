package com.compose_chat.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.compose_chat.data.AndroidAudioPlayer
import com.compose_chat.data.AudioPlayerListener
import com.compose_chat.domain.AudioPlayer
import com.compose_chat.domain.ChatUser
import com.compose_chat.domain.Message
import com.compose_chat.domain.MessageData
import com.compose_chat.ui.components.InputField
import com.compose_chat.ui.components.MessageBubble
import java.time.LocalDateTime

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun ChatView(
    modifier: Modifier = Modifier,
    messageList: List<Message>,
    loggedInUser: ChatUser,
    recipient: ChatUser,
    onMessageSend: (Message) -> Unit,
    composeChatStyle: ComposeChatStyle = defaultComposeChatStyle
) {
    var audioProgress: Float by remember {
        mutableFloatStateOf(0.0f)
    }
    val context = LocalContext.current
    val audioPlayer: AudioPlayer by lazy {
        AndroidAudioPlayer(context, listener = object : AudioPlayerListener {
            override fun onProgressUpdate(progressValue: Int, currentPlayingResource: String?) {
                Log.d("🔊", "onProgressUpdate: $progressValue")
                audioProgress = progressValue.toFloat() / 100

            }

        })
    }
    val reversedMessageList = messageList.reversed()
    Scaffold { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .background(color = composeChatStyle.backGroundColor)
        ) {
            EndlessLazyColumn(items = reversedMessageList,
                itemKey = { message: Message ->
                    message.id ?: System.currentTimeMillis().toString()
                },
                loadMore = {
                    Log.d("🔊", "loadMore: ")

                },
                itemContent = { message ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = LinearOutSlowInEasing
                                )
                            )
                    ) {
                        MessageBubble(
                            modifier = Modifier.padding(8.dp),
                            message = message,
                            isSender = message.author.id == loggedInUser.id,
                            audioPlayer = audioPlayer,
                            mediaProgress = audioProgress,
                            bubbleStyle = getChatBubbleStyle(
                                bubleUserId = if (message.author.id == loggedInUser.id) message.author.id else recipient.id,
                                chatBubbleStyles = composeChatStyle.chatBubbleStyles,
                                loggedInUserId = loggedInUser.id,

                                ),
                            composeChatStyle = composeChatStyle,

                            isAudioPlaying = audioPlayer.playingResource == message.messageData.url
                        )
                    }
                }, loadingItem = { /*TODO*/ })
            InputField(
                modifier = Modifier.align(alignment = androidx.compose.ui.Alignment.BottomCenter),
                loggedInUser = loggedInUser,
                recipient = recipient,
                inputFieldStyle = composeChatStyle.inputFieldStyle,
                onSendClick = {
                    onMessageSend(
                        it
                    )

                })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "ChatView")
@Composable
private fun PreviewChatView() {
    ChatView(
        composeChatStyle = defaultComposeChatStyle,
        onMessageSend = {},
        loggedInUser = ChatUser(
            id = "1",
            name = "John",
            imageUrl = "https://randomuser.me/api/port"
        ),
        recipient = ChatUser(
            id = "2",
            name = "Jane",
            imageUrl = "https://randomuser.me/api/port"
        ),
        messageList = listOf(
            Message(
                recipient = com.compose_chat.domain.ChatUser(
                    id = "1",
                    name = "Jane",
                    imageUrl = "https://randomuser.me/api/port"
                ),
                messageData = MessageData("Hi"),
                timestamp = LocalDateTime.now(),
                id = "",
                author = com.compose_chat.domain.ChatUser(
                    id = "1",
                    name = "John",
                    imageUrl = "https://randomuser.me/api/port"
                ),


                ),
            Message(
                recipient = com.compose_chat.domain.ChatUser(
                    id = "1",
                    name = "Jane",
                    imageUrl = "https://randomuser.me/api/port"
                ),
                messageData = MessageData("Hello"),
                timestamp = LocalDateTime.now(),
                id = "",
                author = com.compose_chat.domain.ChatUser(
                    id = "2",
                    name = "John",
                    imageUrl = "https://randomuser.me/api/port"
                )
            ),
            Message(
                recipient = com.compose_chat.domain.ChatUser(
                    id = "1",
                    name = "Jane",
                    imageUrl = "https://randomuser.me/api/port"
                ),
                messageData = MessageData("how are you"),
                timestamp = LocalDateTime.now(),
                id = "",
                author = com.compose_chat.domain.ChatUser(
                    id = "1",
                    name = "John",
                    imageUrl = "https://randomuser.me/api/port"
                )
            ),
            Message(
                recipient = com.compose_chat.domain.ChatUser(
                    id = "2",
                    name = "Jane",
                    imageUrl = "https://randomuser.me/api/port"
                ),
                messageData = MessageData("I am good"),
                timestamp = LocalDateTime.now(),
                id = "",
                author = com.compose_chat.domain.ChatUser(
                    id = "2",
                    name = "John",
                    imageUrl = "https://randomuser.me/api/port"
                )
            ),

            )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun <T> EndlessLazyColumn(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    items: List<T>,
    itemKey: (T) -> String,
    itemContent: @Composable (T) -> Unit,
    loadingItem: @Composable () -> Unit,
    loadMore: () -> Unit
) {

    val reachedBottom: Boolean by remember { derivedStateOf { listState.reachedBottom() } }

    // load more if scrolled to bottom
    LaunchedEffect(reachedBottom) {
        if (reachedBottom && !loading) loadMore()
    }
    LaunchedEffect(items) {
        listState.animateScrollToItem(0)
    }


    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .padding(bottom = 64.dp),
        state = listState,
        reverseLayout = true
    ) {
        items(
            items = items,
//            key = { item: T -> itemKey(item) },
        ) { item ->

            Box(modifier = Modifier.animateItemPlacement()) {
                itemContent(item)
            }
        }

        if (loading) {
            item {
                loadingItem()
            }
        }
    }
}


private fun LazyListState.reachedBottom(): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - 1
}