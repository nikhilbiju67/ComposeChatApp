package com.compose_chat.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.compose_chat.R
import com.compose_chat.data.AndroidAudioPlayer
import com.compose_chat.data.AudioPlayerListener
import com.compose_chat.domain.AudioPlayer
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.model.AmplitudeType
import java.io.File

val amplitudes=   listOf(
    8, 5, 6, 8, 7, 8, 4, 2, 8, 4, 3,
    8, 5, 6, 8, 7, 8, 4, 2, 8, 4, 3,
    2, 9, 8, 9, 9, 2, 9, 4, 9, 8, 5, 6, 8, 7, 8, 4, 2, 8, 4, 3,
    8, 5, 6, 8, 7, 8, 4, 2, 8, 4, 3,
    8, 5, 6, 8, 7, 8, 4, 2, 8, 4, 3,
    2, 9, 8, 9, 9, 2, 9, 4, 9, 8, 5, 6, 8, 7, 8, 4, 2, 8, 4, 3,
    2, 9, 8, 9, 9, 2, 9, 4, 9, 8, 5, 6, 8, 7, 8, 4, 2, 8, 4, 3,
    2, 9, 8, 9, 9, 2, 9, 4, 9, 8, 5, 6, 8, 7, 8, 4, 2, 8, 4, 3,
    2, 9, 8, 9, 9, 2, 9, 4, 9, 8, 5, 6, 8, 7, 8, 4, 2, 8, 4, 3,
    2, 9, 8, 9, 9, 2, 9, 4, 9, 8, 5, 6, 8, 7, 8, 4, 2, 8, 4, 3
)
@Composable
fun AudioPlayerWidget(
    modifier: Modifier = Modifier,
    audioRemoteUrl: String? = null,
    audioLocalFile: File? = null,
    audioPlayer: AudioPlayer,
    isAudioPlaying: Boolean = false,
    mediaProgress: Float = 0f,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val context = LocalContext.current
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                audioPlayer.stopAudio()

                Log.d("ON_START", "MatchesView:")

            } else if (event == Lifecycle.Event.ON_STOP) {
                Log.d("ON_STOP", "MatchesView:")
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)) {
        Column {


            Row(verticalAlignment = Alignment.CenterVertically) {
                ///audio playButton
                Image(
                    painter = painterResource(id = R.drawable.baseline_play_circle_24),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    contentDescription = "play",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(32.dp)
                        .clickable {
                            if (audioRemoteUrl != null || audioLocalFile != null)
                                audioPlayer.playAudio(audioLocalFile, audioRemoteUrl)
                        }
                )
                AudioWaveform(
                    amplitudes = amplitudes,
                    amplitudeType = AmplitudeType.Avg,
                    progressBrush = SolidColor(Color.White),
                    waveformBrush = SolidColor(Color.Black),
                    spikeWidth = 4.dp,
                    spikePadding = 2.dp,
                    spikeRadius = 4.dp,

//                    progress = 0.6f,
                    progress = if (isAudioPlaying) mediaProgress else 0f,
                    onProgressChange = {  }
                )

//                ///audio seekbar
//                WavySlider(
//                    value = if (isAudioPlaying) mediaProgress else 0f,
//                    onValueChange = { },
//                    colors = SliderDefaults.colors(
//                        activeTrackColor = MaterialTheme.colorScheme.primary,
//                         activeTickColor = MaterialTheme.colorScheme.primary,
//                        thumbColor = MaterialTheme.colorScheme.primary
//                    ),
//                    waveLength = 26.dp,     // Set this to 0.dp to get a regular Slider
//                    waveHeight = 12.dp,     // Set this to 0.dp to get a regular Slider
//                    waveVelocity = 15.dp to WaveDirection.HEAD,
//                    waveThickness = 2.dp,   // Defaults to the specified trackThickness
//                    trackThickness = 3.dp,  // Defaults to 4.dp, same as regular Slider
//                    incremental = true,    // Whether to gradually increase waveHeight
//                    // animationSpecs = ... // Customize various animations of the wave
//                )

            }
        }
    }
}

@Preview(name = "AudioPlayerWidget")
@Composable
private fun PreviewAudioPlayerWidget() {
    AudioPlayerWidget(
        audioPlayer = AndroidAudioPlayer.getInstance(
            LocalContext.current,
            object : AudioPlayerListener {
                override fun onProgressUpdate(progress: Int, currentPlayingResource: String?) {
                    Log.d("🔊", "onProgressUpdate: $progress")
                }
            })
    )
}