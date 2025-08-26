package com.compdfkit.conversion.component

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.compdfkit.conversion.base.ExcelOptions
import com.compdfkit.conversion.base.ExcelWorksheetOption
import com.compdfkit.conversion.base.HtmlOptions
import com.compdfkit.conversion.base.HtmlPageOption
import com.compdfkit.conversion.base.ImageColorMode
import com.compdfkit.conversion.base.ImageOptions
import com.compdfkit.conversion.base.ImageType
import com.compdfkit.conversion.base.JsonOptions
import com.compdfkit.conversion.base.MarkdownOptions
import com.compdfkit.conversion.base.OCRLanguage
import com.compdfkit.conversion.base.PageLayoutMode
import com.compdfkit.conversion.base.PptOptions
import com.compdfkit.conversion.base.RtfOptions
import com.compdfkit.conversion.base.SearchablePdfOptions
import com.compdfkit.conversion.base.TxtOptions
import com.compdfkit.conversion.base.WordOptions
import com.compdfkit.conversion.entity.ConversionStatus
import com.compdfkit.conversion.entity.ConversionTask
import com.compdfkit.conversion.entity.ConversionType
import com.compdfkit.conversion.ui.theme.ComPDFKitConversionDemoTheme
import com.compdfkit.conversion.utils.PathUtils
import java.util.UUID

@Composable
private fun OptionsCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onCheckedChange(!checked) }
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun FilePathSelector(
    filePath: String,
    onPathChanged: (String) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val tempFile = PathUtils.copyUriToInternalFile(context, uri)
            onPathChanged(tempFile?.path ?: "")
        }
    }

    OutlinedTextField(
        value = filePath,
        onValueChange = onPathChanged,
        label = { Text("PDF Path") },
        trailingIcon = {
            IconButton(onClick = {
                launcher.launch(arrayOf("application/pdf", "image/*"))
            }) {
                Icon(Icons.Default.Folder, "File Select")
            }
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        readOnly = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun <reified T : Enum<T>> EnumDropdown(
    label: String,
    selected: T,
    noinline onSelected: (T) -> Unit,
    expanded: Boolean,
    crossinline onExpandedChange: (Boolean) -> Unit,
    noinline filter: (T) -> Boolean = { true }
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandedChange(!expanded) }
    ) {
        OutlinedTextField(
            value = selected.name,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            singleLine = true,
            readOnly = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            enumValues<T>()
                .filter(filter)
                .forEach { value ->
                    DropdownMenuItem(
                        text = { Text(value.name) },
                        onClick = {
                            onSelected(value)
                            onExpandedChange(false)
                        }
                    )
                }
        }
    }
}

@Composable
fun LabeledSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0.5f..2.0f,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Image Scale: ${"%.0f".format(value * 100)}%",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(8.dp))

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.weight(1f)
        )
    }
}

fun checkPageRangesFormat(pageRanges: String): Boolean {
    val pageRangePattern = Regex("""^(\d+(-\d+)?)(,(\d+(-\d+)?))*$""")
    return pageRanges.isEmpty() || pageRangePattern.matches(pageRanges)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDialog(
    title: String,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (ConversionTask) -> Unit,
    task: ConversionTask? = null
) {
    var filePath by remember { mutableStateOf("") }
    var conversionType by remember { mutableStateOf(ConversionType.WORD) }
    var typeExpanded by remember { mutableStateOf(false) }

    var enableAiLayout by remember { mutableStateOf(true) }
    var containImage by remember { mutableStateOf(true) }
    var formulaToImage by remember { mutableStateOf(false) }
    var jsonContainTable by remember { mutableStateOf(true) }
    var containAnnotation by remember { mutableStateOf(true) }
    var excelAllContent by remember { mutableStateOf(false) }
    var excelCsvFormat by remember { mutableStateOf(false) }
    var enableOcr by remember { mutableStateOf(false) }
    var txtTableFormat by remember { mutableStateOf(true) }
    var imagePathEnhance by remember { mutableStateOf(true) }
    var imageScaling by remember { mutableFloatStateOf(1.0f) }
    var pageLayoutMode by remember { mutableStateOf(PageLayoutMode.FLOW) }
    var excelWorksheetOption by remember { mutableStateOf(ExcelWorksheetOption.FOR_TABLE) }
    var htmlPageOption by remember { mutableStateOf(HtmlPageOption.SINGLE_PAGE) }
    var imageColorMode by remember { mutableStateOf(ImageColorMode.COLOR) }
    var imageType by remember { mutableStateOf(ImageType.JPG) }
    var pageRanges by remember { mutableStateOf("") }
    var isFormatValid by remember { mutableStateOf(true) }

    var ocrLanguage by remember { mutableStateOf(OCRLanguage.AUTO) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = {
                Column(Modifier.padding(vertical = 8.dp)) {
                    FilePathSelector(
                        filePath = filePath,
                        onPathChanged = { filePath = it }
                    )

                    Spacer(Modifier.height(16.dp))

                    EnumDropdown(
                        label = "Conversion Type",
                        selected = conversionType,
                        onSelected = {
                            conversionType = it
                        },
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = it }
                    )

                    Spacer(Modifier.height(8.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var layoutModeExpanded by remember { mutableStateOf(false) }
                        var htmlPageExpanded by remember { mutableStateOf(false) }
                        var excelWorksheetExpanded by remember { mutableStateOf(false) }
                        var imageModeExpanded by remember { mutableStateOf(false) }
                        var imageTypeExpanded by remember { mutableStateOf(false) }
                        var languageExpanded by remember { mutableStateOf(false) }

                        if (conversionType != ConversionType.IMAGE &&
                            conversionType != ConversionType.SEARCHABLE_PDF) {
                            OptionsCheckbox(
                                checked = enableAiLayout,
                                onCheckedChange = { enableAiLayout = it },
                                label = "Enable AI Layout"
                            )
                        }

                        if (conversionType != ConversionType.IMAGE &&
                            conversionType != ConversionType.TXT) {
                            OptionsCheckbox(
                                checked = containImage,
                                onCheckedChange = { containImage = it },
                                label = "Contain Image"
                            )
                        }

                        if (conversionType == ConversionType.JSON) {
                            OptionsCheckbox(
                                checked = jsonContainTable,
                                onCheckedChange = { jsonContainTable = it },
                                label = "JSON Contain Table"
                            )
                        }

                        if (conversionType != ConversionType.IMAGE &&
                            conversionType != ConversionType.TXT &&
                            conversionType != ConversionType.SEARCHABLE_PDF) {
                            OptionsCheckbox(
                                checked = containAnnotation,
                                onCheckedChange = { containAnnotation = it },
                                label = "Contain Annotation"
                            )
                        }

                        if (conversionType != ConversionType.IMAGE &&
                            conversionType != ConversionType.TXT &&
                            conversionType != ConversionType.JSON &&
                            conversionType != ConversionType.MARKDOWN) {
                            OptionsCheckbox(
                                checked = formulaToImage,
                                onCheckedChange = { formulaToImage = it },
                                label = "Formula To Image"
                            )
                        }

                        if (conversionType == ConversionType.EXCEL) {
                            OptionsCheckbox(
                                checked = excelAllContent,
                                onCheckedChange = { excelAllContent = it },
                                label = "All Content"
                            )

                            OptionsCheckbox(
                                checked = excelCsvFormat,
                                onCheckedChange = { excelCsvFormat = it },
                                label = "CSV Format"
                            )

                            EnumDropdown(
                                label = "Excel Worksheet Option",
                                selected = excelWorksheetOption,
                                onSelected = {
                                    excelWorksheetOption = it
                                    excelWorksheetExpanded = false
                                },
                                expanded = excelWorksheetExpanded,
                                onExpandedChange = { excelWorksheetExpanded = it }
                            )
                        }

                        if (conversionType != ConversionType.IMAGE &&
                            conversionType != ConversionType.SEARCHABLE_PDF) {
                            OptionsCheckbox(
                                checked = enableOcr,
                                onCheckedChange = { enableOcr = it },
                                label = "Enable OCR"
                            )
                        }

                        if (enableOcr) {
                            EnumDropdown(
                                label = "OCR Language",
                                selected = ocrLanguage,
                                onSelected = {
                                    ocrLanguage = it
                                    languageExpanded = false
                                },
                                expanded = languageExpanded,
                                onExpandedChange = { languageExpanded = it },
                                filter = { it != OCRLanguage.UNKNOWN }
                            )
                        }

                        if (conversionType == ConversionType.TXT) {
                            OptionsCheckbox(
                                checked = txtTableFormat,
                                onCheckedChange = { txtTableFormat = it },
                                label = "Txt Table Format"
                            )
                        }

                        if (conversionType == ConversionType.WORD ||
                            conversionType == ConversionType.HTML) {
                            EnumDropdown(
                                label = "Page Layout Mode",
                                selected = pageLayoutMode,
                                onSelected = {
                                    pageLayoutMode = it
                                    layoutModeExpanded = false
                                },
                                expanded = layoutModeExpanded,
                                onExpandedChange = { layoutModeExpanded = it }
                            )

                            if (conversionType == ConversionType.HTML) {
                                EnumDropdown(
                                    label = "Html Page Option",
                                    selected = htmlPageOption,
                                    onSelected = {
                                        htmlPageOption = it
                                        htmlPageExpanded = false
                                    },
                                    expanded = htmlPageExpanded,
                                    onExpandedChange = { htmlPageExpanded = it }
                                )
                            }
                        }

                        if (conversionType == ConversionType.IMAGE) {
                            OptionsCheckbox(
                                checked = imagePathEnhance,
                                onCheckedChange = { imagePathEnhance = it },
                                label = "PDF Path Enhance"
                            )

                            LabeledSlider(
                                value = imageScaling,
                                onValueChange = { newValue ->
                                    imageScaling = newValue
                                }
                            )

                            EnumDropdown(
                                label = "Image Color Mode",
                                selected = imageColorMode,
                                onSelected = {
                                    imageColorMode = it
                                    imageModeExpanded = false
                                },
                                expanded = imageModeExpanded,
                                onExpandedChange = { imageModeExpanded = it }
                            )

                            EnumDropdown(
                                label = "Image Type",
                                selected = imageType,
                                onSelected = {
                                    imageType = it
                                    imageTypeExpanded = false
                                },
                                expanded = imageTypeExpanded,
                                onExpandedChange = { imageTypeExpanded = it }
                            )
                        }

                        OutlinedTextField(
                            value = pageRanges,
                            onValueChange = { input ->
                                val filtered = input.filter { it.isDigit() || it == ',' || it == '-' }
                                pageRanges = filtered

                                isFormatValid = checkPageRangesFormat(filtered)
                            },
                            label = { Text("Page Range(e.g.:1-5,8,10-12)") },
                            isError = !isFormatValid,
                            supportingText = {
                                if (!isFormatValid) {
                                    Text("Format Error, e.g.:1-5,8,10-12", color = MaterialTheme.colorScheme.error)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = filePath.isNotEmpty() && isFormatValid,
                    onClick = {
                        val tempOptions = when (conversionType) {
                            ConversionType.WORD -> WordOptions(
                                containImage,
                                containAnnotation,
                                enableAiLayout,
                                formulaToImage,
                                enableOcr,
                                pageLayoutMode,
                                pageRanges
                            )
                            ConversionType.EXCEL -> ExcelOptions(
                                containImage,
                                containAnnotation,
                                enableAiLayout,
                                formulaToImage,
                                enableOcr,
                                pageRanges,
                                excelAllContent,
                                excelCsvFormat,
                                excelWorksheetOption
                            )
                            ConversionType.PPT -> PptOptions(
                                containImage,
                                containAnnotation,
                                enableAiLayout,
                                formulaToImage,
                                enableOcr,
                                pageRanges
                            )
                            ConversionType.HTML -> HtmlOptions(
                                containImage,
                                containAnnotation,
                                enableAiLayout,
                                formulaToImage,
                                enableOcr,
                                pageLayoutMode,
                                pageRanges,
                                htmlPageOption
                            )
                            ConversionType.IMAGE -> ImageOptions(
                                imageType,
                                imageColorMode,
                                imageScaling,
                                imagePathEnhance,
                                pageRanges
                            )
                            ConversionType.MARKDOWN -> MarkdownOptions(
                                containImage,
                                containAnnotation,
                                enableAiLayout,
                                enableOcr,
                                pageRanges
                            )
                            ConversionType.RTF -> RtfOptions(
                                containImage,
                                containAnnotation,
                                enableAiLayout,
                                formulaToImage,
                                enableOcr,
                                pageRanges
                            )
                            ConversionType.TXT -> TxtOptions(
                                enableAiLayout,
                                enableOcr,
                                pageRanges,
                                txtTableFormat
                            )
                            ConversionType.JSON -> JsonOptions(
                                containImage,
                                containAnnotation,
                                enableAiLayout,
                                enableOcr,
                                pageRanges,
                                jsonContainTable
                            )
                            ConversionType.SEARCHABLE_PDF -> SearchablePdfOptions(
                                containImage,
                                true,
                                formulaToImage,
                                pageRanges
                            )
                        }

                        val newTask = ConversionTask(
                            id = UUID.randomUUID().toString(),
                            path = filePath,
                            type = conversionType,
                            status = mutableStateOf(ConversionStatus.READY),
                            progress = mutableIntStateOf(0),
                            completed = mutableIntStateOf(0),
                            options = tempOptions,
                            ocrLanguage = mutableStateOf(ocrLanguage)
                        )
                        Log.d("TaskDialog", "onConfirm: $newTask")
                        onConfirm(newTask)
                        onDismiss()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview
@Composable
fun NewTaskDialogPreview() {
    ComPDFKitConversionDemoTheme {
        TaskDialog(
            title = "Configure Task",
            showDialog = true,
            onDismiss = {},
            onConfirm = {}
        )
    }
}