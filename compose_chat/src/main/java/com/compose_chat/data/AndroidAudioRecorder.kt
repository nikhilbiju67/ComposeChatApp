package com.compose_chat.data

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import com.compose_chat.domain.AudioRecorder
import java.io.File
import java.io.FileOutputStream

class AndroidAudioRecorder(private val context: Context) : AudioRecorder {
    private var audioRecorder: MediaRecorder? = null
    override fun start(outputFile: File) {

        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(outputFile).fd)
            prepare()
            start()
            audioRecorder=this

        }
    }

    private fun createRecorder(): MediaRecorder {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }

    }

    override fun stop() {
        audioRecorder?.apply {
            stop()
            reset()
        }
        audioRecorder=null
    }
}