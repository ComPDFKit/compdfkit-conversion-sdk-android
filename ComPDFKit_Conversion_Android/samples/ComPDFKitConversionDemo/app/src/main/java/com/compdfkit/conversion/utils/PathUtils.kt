package com.compdfkit.conversion.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.RequiresApi
import com.compdfkit.conversion.entity.ConversionType
import java.io.File
import java.net.URLConnection
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class PathUtils {
    companion object {
        fun getFileName(path: String): String {
            val file = File(path)
            return file.name
        }

        fun getNameWithoutPrefixSuffix(path: String): String {
            val file = File(path)
            val nameWithExtension = file.name
            val dotIndex = nameWithExtension.lastIndexOf('.')
            return if (dotIndex != -1) {
                nameWithExtension.substring(0, dotIndex)
            } else {
                nameWithExtension
            }
        }

        fun getFileSuffix(path: String): String {
            val file = File(path)
            val nameWithExtension = file.name
            val dotIndex = nameWithExtension.lastIndexOf('.')

            return if (dotIndex != -1) {
                nameWithExtension.substring(dotIndex)
            } else {
                ""
            }
        }

        fun removeFile(path: String): Boolean {
            val file = File(path)
            return file.delete()
        }

        fun deleteDirectory(dir: File): Boolean {
            if (!dir.exists()) return true
            if (dir.isFile) return dir.delete()

            dir.listFiles()?.forEach { child ->
                deleteDirectory(child)
            }

            return dir.delete()
        }

        fun getOutputPath(pdfFilePath: String, type: ConversionType,
                          isNeedZip: Boolean = false, isCSV: Boolean = false): String {
            val context = AppContextHolder.get()
            val outputRoot = context.getExternalFilesDir(null)?.absolutePath ?: ""

            var suffix = when (type) {
                ConversionType.WORD -> ".docx"
                ConversionType.EXCEL -> ".xlsx"
                ConversionType.PPT -> ".pptx"
                ConversionType.HTML -> ".html"
                ConversionType.IMAGE -> ""
                ConversionType.RTF -> ".rtf"
                ConversionType.TXT -> ".txt"
                ConversionType.JSON -> ".json"
                ConversionType.SEARCHABLE_PDF -> ".pdf"
                ConversionType.MARKDOWN -> ".md"
            }

            if (isCSV) {
                if (isNeedZip)
                    suffix = ""
                else
                    suffix = ".csv"
            }

            val pdfFileName = getNameWithoutPrefixSuffix(pdfFilePath)
            var outputPath = "$outputRoot/$pdfFileName$suffix"
            if (ConversionType.HTML == type) {
                outputPath = "$outputRoot/$pdfFileName/$pdfFileName$suffix"
            }

            val outputDir = File(outputRoot)
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }

            var outputFile = File(outputPath)

            var zipPath = "$outputRoot/$pdfFileName.zip"
            if (isNeedZip || ConversionType.HTML == type) {
                outputFile = File(zipPath)
            }

            Log.d("PathUtils", "outputPath: $outputPath")
            var index = 1
            while (outputFile.exists()) {
                outputPath =
                    "$outputRoot/${pdfFileName}(${index})$suffix"
                outputFile = File(outputPath)
                if (isNeedZip || ConversionType.HTML == type) {
                    zipPath =
                        "$outputRoot/${pdfFileName}(${index}).zip"
                    outputPath = "$outputRoot/${pdfFileName}(${index})/${pdfFileName}(${index})$suffix"
                    outputFile = File(zipPath)
                    Log.d("PathUtils", "File exists: $outputPath, Rename $zipPath")
                } else {
                    Log.d("PathUtils", "File exists: $outputPath, Rename $outputPath")
                }
                index++
            }

            if (isNeedZip && getFileSuffix(outputPath).isBlank()) {
                val imagePath = File(outputPath)
                if (!imagePath.exists()) {
                    if (!imagePath.mkdirs()) {
                        Log.e("PathUtils", "make: $imagePath failed")
                    }
                }
            }

            return outputPath
        }

        fun shareFile(uri: Uri?) {
            if (uri == null)
                return

            val context = AppContextHolder.get()
            val fileName = getFileNameFromUri(context, uri)
            val mimeType = URLConnection.guessContentTypeFromName(fileName)
                ?: "application/octet-stream"

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(
                Intent.createChooser(intent, "Share File").apply {
                    if (context !is Activity) {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
            )
        }

        fun zipFolder(sourceDir: File, outputZip: File) {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(outputZip))).use { zipOut ->
                sourceDir.walkTopDown()
                    .filter { it.isFile }
                    .forEach { file ->
                        val entryName = file.relativeTo(sourceDir).path.replace(File.separatorChar, '/')
                        val entry = ZipEntry(entryName)
                        zipOut.putNextEntry(entry)

                        file.inputStream().use { input ->
                            input.copyTo(zipOut)
                        }

                        zipOut.closeEntry()
                    }
            }
            deleteDirectory(sourceDir)
        }

        fun handleNeedZipOutput(needZip: File): File {
            val context = AppContextHolder.get()
            val outputRoot = context.getExternalFilesDir(null)?.absolutePath ?: ""

            val targetDirName = getNameWithoutPrefixSuffix(needZip.toString())
            val targetPath = "$outputRoot/$targetDirName"

            val zipPath = "$targetPath.zip"

            val zipFile = File(zipPath)
            if (zipFile.exists()) {
                return zipFile
            } else {
                zipFolder(File(targetPath), zipFile)
            }

            return zipFile
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        fun exportOutputToDownloads(srcFile: File,
                                    isHtml: Boolean = false,
                                    isNeedZip: Boolean = false): Uri? {
            Log.d("PathUtils", "exportOutputToDownloads: $srcFile, $isHtml, $isNeedZip")

            var targetFile = srcFile

            if (isNeedZip || isHtml) {
                targetFile = handleNeedZipOutput(targetFile)
            }

            if (!targetFile.exists()) {
                Log.e("PathUtils", "exportOutputToDownloads: targetFile not exists")
                return null
            }

            val context = AppContextHolder.get()
            val resolver = context.contentResolver

            val destFileName = targetFile.name

            val mimeType = URLConnection.guessContentTypeFromName(destFileName)
                ?: "application/octet-stream"
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, destFileName)
                put(MediaStore.Downloads.MIME_TYPE, mimeType)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: return null

            resolver.openOutputStream(uri)?.use { output ->
                if (targetFile.isDirectory)
                    return uri
                targetFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)

            return uri
        }

        fun getFileNameFromUri(context: Context, uri: Uri): String? {
            val returnCursor = context.contentResolver.query(uri, null, null, null, null)
            returnCursor?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex != -1) {
                    return cursor.getString(nameIndex)
                }
            }
            return null
        }

        fun copyUriToInternalFile(context: Context, uri: Uri): File? {
            return try {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return null
                val fileName = getFileNameFromUri(context, uri) ?: return null
                val targetFile = File(context.filesDir, fileName)
                targetFile.outputStream().use { output ->
                    inputStream.copyTo(output)
                }
                targetFile
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}