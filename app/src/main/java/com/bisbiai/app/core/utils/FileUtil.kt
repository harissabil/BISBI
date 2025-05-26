package com.bisbiai.app.core.utils

import android.content.Context
import java.io.File

fun saveByteArrayToFile(context: Context, byteArray: ByteArray, fileName: String = "tts_audio.mp3"): File {
    val file = File(context.cacheDir, fileName)
    file.outputStream().use {
        it.write(byteArray)
    }
    return file
}
