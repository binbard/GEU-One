package com.binbard.geu.geuone.ui.erp

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Tasks


class OCRUtils {
    companion object {
        private val recognizer =
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    fun getText(bitmap: Bitmap): String {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = Tasks.await(recognizer.process(image))

            val text = StringBuilder()
            for (block in result.textBlocks) {
                for (line in block.lines) {
                    text.append(line.text)
                    text.append("\n")
                }
            }
            return text.toString().trim()
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}
