package com.compose_chat.data

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.compose_chat.domain.AudioPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

interface AudioPlayerListener {
    fun onProgressUpdate(progress: Int,currentPlayingResource: String?)
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
                if (file != null&&file.exists()) {
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
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun stopAudio() {
        if (mediaPlayer?.isPlaying == false) {
            return
        }
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()

        stopProgressUpdates()
    }


    private fun startProgressUpdates() {
        mediaPlayer?.let {
            val duration = it.duration
            progressJob = CoroutineScope(Dispatchers.Main).launch {
                while (true) {
                    val currentPosition = it.currentPosition
                    val progress = (currentPosition.toDouble() / duration) * 100
                    listener.onProgressUpdate(progress.toInt(),playingResource)
                    audioProgress = progress.toInt()

                    delay(50) // Update every second
                }
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
    }
}
