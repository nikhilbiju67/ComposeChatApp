package com.compose_chat.data

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import com.compose_chat.domain.AudioPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

interface AudioPlayerListener {
    fun onProgressUpdate(progress: Int, currentPlayingResource: String?)
}

class AndroidAudioPlayer(private val context: Context, private val listener: AudioPlayerListener) :
    AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: kotlinx.coroutines.Job? = null
    override var playingResource: String? = null
    override var audioProgress: Int = 0

    companion object {
        @Volatile
        private var INSTANCE: AndroidAudioPlayer? = null

        fun getInstance(context: Context, listener: AudioPlayerListener): AndroidAudioPlayer {
            return INSTANCE ?: synchronized(this) {
                val instance = AndroidAudioPlayer(context, listener)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun playAudio(file: File?, url: String?) {
        try {
            if (mediaPlayer?.isPlaying == true) {
                stopAudio()

            }

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                if (file != null && file.exists()) {
                    playingResource = file.absolutePath
                    setDataSource(file.absolutePath)
                } else {
                    playingResource = url
                    setDataSource(url)
                }


                setOnPreparedListener { mp ->
                    mp.start()
                    startProgressUpdates()
                }
                setOnCompletionListener {
                    stopProgressUpdates()
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun stopAudio() {
        try {
            if (mediaPlayer?.isPlaying == false) {
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()

            stopProgressUpdates()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AudioPlayer", "stopAudio: ${e.message}")
        }
        stopProgressUpdates()
    }


    private fun startProgressUpdates() {
        progressJob?.cancel()  // Cancels any previous job
        progressJob = CoroutineScope(Dispatchers.Default).launch {
            mediaPlayer?.let { player ->
                val duration = player.duration
                try {
                    while (isActive) {  // Check if coroutine is still active
                        val currentPosition = player.currentPosition
                        val progress = (currentPosition.toDouble() / duration * 100).toInt()

                        withContext(Dispatchers.Main) {
                            listener.onProgressUpdate(progress, playingResource)
                            audioProgress = progress
                        }

                        delay(100)  // Safely delay between progress updates
                    }
                } catch (e: CancellationException) {
                    // Handle coroutine cancellation
                    Log.e("AudioPlayer", "Coroutine cancelled", e)
                } catch (e: Exception) {
                    // Handle other exceptions
                    Log.e("AudioPlayer", "Error updating progress", e)
                }
            }
        }
    }


    private fun stopProgressUpdates() {
        progressJob?.cancel()
    }
}
