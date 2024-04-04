package com.compose_chat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.compose_chat.R
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

const val BlinkTime = 500
@Composable
fun BlinkView(
    modifier: Modifier = Modifier,
    child: @Composable () -> Unit
) {
    var visible by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = child.toString()) {
        while (true) {
            delay(BlinkTime.milliseconds)
            visible = !visible
        }
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(animationSpec = TweenSpec(durationMillis = 0)),
        exit = fadeOut(animationSpec = TweenSpec(durationMillis = 0))
    ) {
       child()
    }

}

@Composable
@Preview
fun BlinkViewPreview() {
    BlinkView {
        Image(
            painter = painterResource(id = R.drawable.baseline_mic_24),
            modifier = Modifier.size(16.dp),
            contentDescription = ""
        )
    }
}
