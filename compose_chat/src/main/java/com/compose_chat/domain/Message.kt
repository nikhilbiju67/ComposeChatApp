package com.compose_chat.domain

import java.io.File
import java.time.LocalDateTime

data class Message(
    val author: ChatUser,
    val id: String?,
    val recipient: ChatUser,
    val timestamp: LocalDateTime,
    val isRead: Boolean = false,
    val messageData: MessageData,
    val status: MessageStatus = MessageStatus.SENT

)

class MessageData(
    val message: String?=null,
    val file:File?=null,
    val url:String?=null,
    val type: MessageType = MessageType.TEXT,
)

enum class MessageType {
    TEXT, IMAGE, VIDEO, AUDIO,UNKNOWN
}

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ
}
