package com.compdfkit.conversion

import android.content.Context
import android.util.Log
import com.compdfkit.conversion.base.OCRLanguage
import java.io.File
import java.io.FileOutputStream

class ConversionHelper {
    companion object {
        var isInstalled: Boolean = false

        fun installAIModel(context: Context) {
            ConverterManager.initialize()
            ConverterManager.setLogger(true, true);

            val assetManager = context.assets
            val modelInAssets = assetManager.list("resource/models") ?: return

            val destDir = File(context.filesDir, "resource/models")
            if (!destDir.exists()) {
                destDir.mkdirs()
            }

            for (fileName in modelInAssets) {
                val destFile = File(destDir, fileName)
                if (!destFile.exists()) {
                    assetManager.open("resource/models/$fileName").use { input ->
                        FileOutputStream(destFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    Log.d("ConversionHelper", "Copied model: $fileName")
                }
            }
            val modelDir = File(context.filesDir, "resource/models/documentai.model").absolutePath
            ConverterManager.setAIModel(modelDir, OCRLanguage.ENGLISH)
            isInstalled = true
        }
    }
}