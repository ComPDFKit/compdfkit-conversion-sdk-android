package com.compdfkit.conversion.entity

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.compdfkit.conversion.ComPDFKitConverter
import com.compdfkit.conversion.ConversionHelper
import com.compdfkit.conversion.base.ConvertCallback
import com.compdfkit.conversion.base.ErrorCode
import com.compdfkit.conversion.base.ExcelOptions
import com.compdfkit.conversion.base.ExcelWorksheetOption
import com.compdfkit.conversion.base.HtmlOptions
import com.compdfkit.conversion.base.ImageOptions
import com.compdfkit.conversion.base.JsonOptions
import com.compdfkit.conversion.base.MarkdownOptions
import com.compdfkit.conversion.base.OfdOptions
import com.compdfkit.conversion.base.PptOptions
import com.compdfkit.conversion.base.RtfOptions
import com.compdfkit.conversion.base.SearchablePdfOptions
import com.compdfkit.conversion.base.TxtOptions
import com.compdfkit.conversion.base.WordOptions
import com.compdfkit.conversion.utils.AppContextHolder
import com.compdfkit.conversion.utils.PathUtils
import java.io.File

data class ConversionTask(
    var id: String,
    var path: String,
    var type: ConversionType,
    var status: MutableState<ConversionStatus> = mutableStateOf(ConversionStatus.READY),
    var progress: MutableState<Int> = mutableIntStateOf(0),
    var completed: MutableState<Int> = mutableIntStateOf(0),
    var options: Any,
) : ConvertCallback {

    var outputUri: Uri? = null
    var errorCode: ErrorCode? = null
    @Volatile
    var cancelled: Boolean = false

    override fun onProgress(current: Int, total: Int) {
        progress.value = total
        completed.value = current
        Log.d("ConversionTask", "Converting $path: ${completed.value}/${progress.value}")
    }

    override fun isCancelled(): Boolean = cancelled

    @RequiresApi(Build.VERSION_CODES.Q)
    fun startTask() {
        if (!ConversionHelper.isInstalled) {
            ConversionHelper.installAIModel(AppContextHolder.get())
        }

        val isNeedZip = (type == ConversionType.IMAGE) ||
                (type == ConversionType.MARKDOWN) ||
                (type == ConversionType.JSON && (options as JsonOptions).containImage) ||
                (type == ConversionType.EXCEL && (options as ExcelOptions).csvFormat && (options as ExcelOptions).excelWorksheetOption != ExcelWorksheetOption.FOR_DOCUMENT)

        val isCSV = type == ConversionType.EXCEL && (options as ExcelOptions).csvFormat

        val outputPath = PathUtils.getOutputPath(path, type, isNeedZip, isCSV)
        status.value = ConversionStatus.CONVERTING
        errorCode = when(type) {
            ConversionType.WORD ->
                ComPDFKitConverter.startPDFToWord(path, "", outputPath, options as WordOptions, this)
            ConversionType.EXCEL ->
                ComPDFKitConverter.startPDFToExcel(path, "", outputPath, options as ExcelOptions, this)
            ConversionType.PPT ->
                ComPDFKitConverter.startPDFToPpt(path, "", outputPath, options as PptOptions, this)
            ConversionType.HTML ->
                ComPDFKitConverter.startPDFToHtml(path, "", outputPath, options as HtmlOptions, this)
            ConversionType.IMAGE ->
                ComPDFKitConverter.startPDFToImage(path, "", outputPath, options as ImageOptions, this)
            ConversionType.MARKDOWN ->
                ComPDFKitConverter.startPDFToMarkdown(path, "", outputPath, options as MarkdownOptions, this)
            ConversionType.RTF ->
                ComPDFKitConverter.startPDFToRtf(path, "", outputPath, options as RtfOptions, this)
            ConversionType.TXT ->
                ComPDFKitConverter.startPDFToTxt(path, "", outputPath, options as TxtOptions, this)
            ConversionType.JSON ->
                ComPDFKitConverter.startPDFToJson(path, "", outputPath, options as JsonOptions, this)
            ConversionType.SEARCHABLE_PDF ->
                ComPDFKitConverter.startPDFToSearchablePdf(
                    path, "", outputPath, options as SearchablePdfOptions, this)
            ConversionType.OFD ->
                ComPDFKitConverter.startPDFToOfd(
                    path, "", outputPath, options as OfdOptions, this)
        }

        status.value = if (errorCode == ErrorCode.SUCCESS) {
            ConversionStatus.SUCCESS
        } else {
            ConversionStatus.FAILED
        }

        outputUri = PathUtils.exportOutputToDownloads(File(outputPath),
            isHtml = type == ConversionType.HTML,
            isNeedZip = isNeedZip)
    }
}
