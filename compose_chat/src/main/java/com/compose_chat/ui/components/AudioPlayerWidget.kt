package com.compose_chat.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.compose_chat.R
import com.compose_chat.data.AndroidAudioPlayer
import com.compose_chat.data.AudioPlayerListener
import com.compose_chat.domain.AudioPlayer
import ir.mahozad.multiplatform.wavyslider.WaveDirection
import ir.mahozad.multiplatform.wavyslider.material.WavySlider
import java.io.File

@Composable
fun AudioPlayerWidget(
    modifier: Modifier = Modifier,
    audioRemoteUrl: String? = null,
    audioLocalFile: File? = null,
    audioPlayer: AudioPlayer,
    isAudioPlaying: Boolean = false,
    mediaProgress: Float = 0f
) {
    val context = LocalContext.current


    Box(modifier.height(30.dp)) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ///audio playButton
                Image(
                    painter = painterResource(id = R.drawable.play_button),
                    contentDescription = "play",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(24.dp)
                        .clickable {
                            if (audioRemoteUrl != null || audioLocalFile != null)
                                audioPlayer.playAudio(audioLocalFile, audioRemoteUrl)
                        }
                )


                ///audio seekbar
                WavySlider(
                    value = if (isAudioPlaying) mediaProgress else 0f,
                    onValueChange = { },
                    waveLength = 26.dp,     // Set this to 0.dp to get a regular Slider
                    waveHeight = 8.dp,     // Set this to 0.dp to get a regular Slider
                    waveVelocity = 15.dp to WaveDirection.TAIL,
                    waveThickness = 1.dp,   // Defaults to the specified trackThickness
                    trackThickness = 1.dp,  // Defaults to 4.dp, same as regular Slider
                    incremental = true,    // Whether to gradually increase waveHeight
                    // animationSpecs = ... // Customize various animations of the wave
                )

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