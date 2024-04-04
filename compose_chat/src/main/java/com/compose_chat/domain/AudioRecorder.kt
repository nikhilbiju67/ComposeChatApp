package com.compose_chat.domain

import java.io.File

interface AudioRecorder {
    fun start(outputFile:File)
    fun stop()
}