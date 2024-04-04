package com.compose_chat.domain

import java.io.File

interface AudioPlayer {
    var playingResource: String?
    fun playAudio(file: File?,url: String?)
    fun stopAudio()
    var audioProgress : Int
}