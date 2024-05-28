package com.compose_chat.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose_chat.utils.ComposeChatDateFormat
import com.smarttoolfactory.bubble.ArrowAlignment
import com.smarttoolfactory.bubble.ArrowShape
import com.smarttoolfactory.bubble.BubbleCornerRadius
import com.smarttoolfactory.bubble.BubbleState


data class ComposeChatStyle(
    val inputFieldStyle: InputFieldStyle = defaultInputFieldStyle,
    val backGroundColor: Color,
    val bubbleTimeFormater: ComposeChatDateFormat = ComposeChatDateFormat.Relative,

    val chatBubbleStyles: List<ChatBubbleStyle> = emptyList(),

    )

data class InputFieldStyle(
    val backGroundColor: Color,
    val inputTextStyle: TextStyle,
    val micIconColor: Color,
    val sendButtonIconColor: Color,
    val textFieldShape: Shape = RoundedCornerShape(20.dp),
    val focusedIndicatorColor: Color = Color.Transparent,
    val unfocusedIndicatorColor: Color = Color.Transparent,
    val focusedContainerColor: Color = Color.White,
    val unfocusedContainerColor: Color = Color.White,
    val sendButtonBackGroundColor: Color = Color.Blue,
    val micButtonBackGroundColor: Color = Color.Blue,
)

data class ChatBubbleStyle(
    val userId: String,
    val backGroundColor: Color,
    val textColor: Color,
    val timeTextStyle: TextStyle,
    val messageTextStyle: TextStyle = TextStyle(
        color = Color.Black,
        fontSize = 16.sp
    ),

    )

val defaultInputFieldStyle = InputFieldStyle(
    backGroundColor = Color.White,
    inputTextStyle = TextStyle(
        color = Color.Black,
        fontSize = 16.sp
    ),
    micIconColor = Color.Black,
    sendButtonIconColor = Color.Black,

    )


val defaultComposeChatStyle = ComposeChatStyle(
    inputFieldStyle = defaultInputFieldStyle,
    backGroundColor = Color.White,

    chatBubbleStyles = listOf(
        ChatBubbleStyle(
            userId = "1",
            backGroundColor = Color.Blue,
            textColor = Color.White,
            timeTextStyle = TextStyle(
                color = Color.White,
                fontSize = 12.sp
            ),

            ),
        ChatBubbleStyle(
            userId = "2",
            backGroundColor = Color.Green,
            textColor = Color.White,
            timeTextStyle = TextStyle(
                color = Color.White,
                fontSize = 12.sp
            )
        )
    )
)

fun getChatBubbleStyle(
    bubleUserId: String,
    chatBubbleStyles: List<ChatBubbleStyle>,
    loggedInUserId: String
): ChatBubbleStyle {
    val chatBubbleStyle = chatBubbleStyles.firstOrNull() { it.userId == bubleUserId }
    if (chatBubbleStyle != null) {
        return chatBubbleStyle
    }
    return if (bubleUserId == loggedInUserId) {
        defaultSenderChatBubbleStyle
    } else {
        defaultReceiverChatBubbleStyle
    }
}

val defaultSenderChatBubbleStyle = ChatBubbleStyle(
    userId = "1",
    backGroundColor = Color.Blue,
    textColor = Color.White,
    timeTextStyle = TextStyle(
        color = Color.Gray,
        fontSize = 12.sp
    ),
    messageTextStyle = TextStyle(
        color = Color.White,
        fontSize = 16.sp
    )
)
val defaultReceiverChatBubbleStyle = ChatBubbleStyle(
    userId = "2",
    backGroundColor = Color.Green,
    textColor = Color.White,
    timeTextStyle = TextStyle(
        color = Color.Gray,
        fontSize = 12.sp
    ),
    messageTextStyle = TextStyle(
        color = Color.White,
        fontSize = 16.sp
    )
)
val incomingBubbleShape = BubbleState(
    arrowShape = ArrowShape.Curved,

    cornerRadius = BubbleCornerRadius(12.dp, 20.dp, 12.dp, 16.dp),
    alignment = ArrowAlignment.RightBottom,


    )
val outgoingBubbleShape = BubbleState(
    arrowShape = ArrowShape.Curved,

    cornerRadius =

    BubbleCornerRadius(20.dp, 12.dp, 16.dp, 12.dp),
    alignment = ArrowAlignment.LeftBottom,


    )