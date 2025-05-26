package com.bisbiai.app.core.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.bisbiai.app.data.remote.dto.BoundingBox
import java.io.File

fun cropImageByBoundingBox(imageFile: File, boundingBox: BoundingBox): Bitmap? {
    val bitmap = BitmapFactory.decodeFile(imageFile.path) ?: return null

    // Pastikan koordinat crop berada dalam batas bitmap
    val x = boundingBox.x.coerceAtLeast(0)
    val y = boundingBox.y.coerceAtLeast(0)
    val width = boundingBox.width.coerceAtMost(bitmap.width - x)
    val height = boundingBox.height.coerceAtMost(bitmap.height - y)

    return Bitmap.createBitmap(bitmap, x, y, width, height)
}

fun saveBitmapToFile(bitmap: Bitmap): File {
    val file = File.createTempFile("cropped_", ".jpg") // Atau gunakan context.cacheDir jika kamu butuh
    val outputStream = file.outputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
    outputStream.flush()
    outputStream.close()
    return file
}