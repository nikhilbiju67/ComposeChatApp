package com.example.composechatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.compose_chat.domain.ChatUser
import com.compose_chat.domain.Message
import com.compose_chat.domain.MessageData
import com.compose_chat.domain.MessageType
import com.compose_chat.ui.ChatView
import com.compose_chat.ui.ComposeChatStyle
import com.compose_chat.ui.InputFieldStyle
import com.example.composechatapp.ui.theme.ComposeChatAppTheme
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeChatAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatView(messageList = emptyList(), loggedInUser = ChatUser(
                        id = "1",
                        name = "Nikhil",
                        imageUrl = "https://randomuser.me/api/port",
                    ), recipient =
                    ChatUser(
                        id = "2",
                        name = "Jane",
                        imageUrl = "https://randomuser.me/api/port",
                    ), onMessageSend = {})
                }
            }
        }
    }
}

@Composable
fun ComposeChatApp() {
    ComposeChatAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ChatView(
                messageList = listOf(
                    Message(
                        recipient = ChatUser(
                            id = "1",
                            name = "Jane",
                            imageUrl = "https://randomuser.me/api/port"
                        ),
                        messageData = MessageData(
                            "Hi",

                            type = MessageType.TEXT,
                        ),
                        timestamp = LocalDateTime.now(),
                        id = "",
                        author = ChatUser(
                            id = "2",
                            name = "John",
                            imageUrl = "https://randomuser.me/api/port"
                        ),

                        ),
                    Message(
                        recipient = ChatUser(
                            id = "2",
                            name = "Jane",
                            imageUrl = "https://randomuser.me/api/port"
                        ),
                        messageData = MessageData(
                            "Hi",

                            type = MessageType.TEXT,
                        ),
                        timestamp = LocalDateTime.now(),
                        id = "",
                        author = ChatUser(
                            id = "1",
                            name = "John",
                            imageUrl = "https://randomuser.me/api/port"
                        ),

                        ),
                    Message(
                        recipient = ChatUser(
                            id = "1",
                            name = "Jane",
                            imageUrl = "https://randomuser.me/api/port"
                        ),
                        messageData = MessageData(
                            "Hello",

                            type = MessageType.TEXT,
                        ),
                        timestamp = LocalDateTime.now(),
                        id = "",
                        author = ChatUser(
                            id = "2",
                            name = "John",
                            imageUrl = "https://randomuser.me/api/port"
                        ),

                        ),
                    Message(
                        recipient = ChatUser(
                            id = "3",
                            name = "Jane",
                            imageUrl = "https://randomuser.me/api/port"
                        ),
                        messageData = MessageData(
                            "How are you?",

                            type = MessageType.TEXT,
                        ),
                        timestamp = LocalDateTime.now(),
                        id = "",
                        author = ChatUser(
                            id = "3",
                            name = "John",
                            imageUrl = "https://randomuser.me/api/port"
                        ),

                        ),
                ), loggedInUser = ChatUser(
                    id = "1",
                    name = "Nikhil",
                    imageUrl = "https://randomuser.me/api/port",
                ), recipient =
                ChatUser(
                    id = "2",
                    name = "Jane",
                    imageUrl = "https://randomuser.me/api/port",
                ), onMessageSend = {}, composeChatStyle = ComposeChatStyle(
                    inputFieldStyle = InputFieldStyle(
                        focusedContainerColor = Color.White,
                        backGroundColor = Color.White,
                        micIconColor = Color.Yellow,
                        focusedIndicatorColor = Color.Gray,
                        inputTextStyle = MaterialTheme.typography.bodySmall,
                        textFieldShape = RoundedCornerShape(20.dp),
                        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedIndicatorColor = Color.Transparent,
                        sendButtonIconColor = Color.Cyan,

                        ),
                    backGroundColor = Color.White,
                    chatBubbleStyles = listOf(
                        com.compose_chat.ui.ChatBubbleStyle(
                            userId = "1",
                            backGroundColor = Color.Red,
                            textColor = Color.White,
                            timeTextStyle = MaterialTheme.typography.bodySmall
                        ),
                        com.compose_chat.ui.ChatBubbleStyle(
                            userId = "2",
                            backGroundColor = Color.Cyan,
                            textColor = Color.White,
                            timeTextStyle = MaterialTheme.typography.bodySmall
                        ),
                        com.compose_chat.ui.ChatBubbleStyle(
                            userId = "3",
                            backGroundColor = Color.Gray,
                            textColor = Color.White,
                            timeTextStyle = MaterialTheme.typography.bodySmall
                        )
                    )
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeChatApp()
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeChatAppTheme {
        Greeting("Android")
    }
}