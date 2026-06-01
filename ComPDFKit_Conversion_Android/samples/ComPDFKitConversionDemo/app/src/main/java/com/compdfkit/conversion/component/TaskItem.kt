package com.compdfkit.conversion.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoDisturbAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.compdfkit.conversion.entity.ConversionStatus
import com.compdfkit.conversion.entity.ConversionTask
import com.compdfkit.conversion.utils.PathUtils

@Composable
fun TaskItem(
    task: ConversionTask,
    isEnableConvert: Boolean = true,
    modifier: Modifier = Modifier,
    onStart: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = PathUtils.getFileName(task.path),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${task.type}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(3f),
                    textAlign = TextAlign.Start
                )

                Text(
                    text =
                        if (task.status.value == ConversionStatus.FAILED)
                            "${task.errorCode}" else "${task.status.value}",
                    style = MaterialTheme.typography.labelSmall,
                    color = when (task.status.value) {
                        ConversionStatus.READY -> MaterialTheme.colorScheme.onSurfaceVariant
                        ConversionStatus.CONVERTING -> MaterialTheme.colorScheme.primary
                        ConversionStatus.SUCCESS -> MaterialTheme.colorScheme.tertiary
                        ConversionStatus.FAILED -> MaterialTheme.colorScheme.error
                    },
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.Start
                )

                val progress: Float = if (task.status.value != ConversionStatus.SUCCESS) {
                    if (task.completed.value > 0) {
                        task.completed.value.toFloat() / task.progress.value.toFloat() * 100
                    } else {
                        0f
                    }
                } else {
                    100f
                }

                Text(
                    text = "${"%.1f".format(progress)}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
            }
        }

        if (task.status.value == ConversionStatus.SUCCESS) {
            IconButton(
                onClick = {
                    PathUtils.shareFile(task.outputUri)
                },
            ) {
                Icon(
                    Icons.Filled.Share,
                    contentDescription = "Share",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        IconButton(
            onClick = {
                onStart()
            },
            enabled = (task.status.value != ConversionStatus.CONVERTING) && isEnableConvert,
            modifier = Modifier.size(36.dp)
        ) {
            if (!isEnableConvert && task.status.value != ConversionStatus.CONVERTING) {
                Icon(
                    Icons.Filled.DoDisturbAlt,
                    contentDescription = "Disabled",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                when (task.status.value) {
                    ConversionStatus.READY -> Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = "Start Task",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    ConversionStatus.CONVERTING -> CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    ConversionStatus.SUCCESS -> Icon(
                        Icons.Filled.Refresh,
                        contentDescription = "Start Task",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    ConversionStatus.FAILED -> Icon(
                        Icons.Filled.Refresh,
                        contentDescription = "Start Task",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}