package com.compdfkit.conversion.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compdfkit.conversion.ComPDFKitConverter
import com.compdfkit.conversion.entity.ConversionStatus
import com.compdfkit.conversion.model.MainViewModel
import com.compdfkit.conversion.ui.theme.ComPDFKitConversionDemoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConvertMainPage() {
    val viewModel: MainViewModel = viewModel()
    val runningTaskId = viewModel.runningTaskId
    val isBatchRunning = viewModel.isBatchRunning

    var isShowNewTaskDialog by remember { mutableStateOf(false) }
    val tasks = viewModel.tasks

    TaskDialog(
        title = "New Task",
        showDialog = isShowNewTaskDialog,
        onDismiss = { isShowNewTaskDialog = false },
        onConfirm = {
            tasks.add(it)
            isShowNewTaskDialog = false
        }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = { Text("ComPDF Conversion SDK v3.2.0") },
                actions = {
                    IconButton(
                        onClick = {
                            tasks.clear()
                            ComPDFKitConverter.cancel()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteSweep,
                            contentDescription = "Clear Tasks"
                        )
                    }

                    IconButton(
                        onClick = {
                            if (isBatchRunning.value)
                                return@IconButton

                            isBatchRunning.value = true

                            CoroutineScope(Dispatchers.IO).launch {
                                for (task in tasks) {
                                    if (task.status.value != ConversionStatus.READY)
                                        continue

                                    task.status.value = ConversionStatus.CONVERTING
                                    task.startTask()
                                }

                                withContext(Dispatchers.Main) {
                                    isBatchRunning.value = false
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
                            contentDescription = "Task Export"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isShowNewTaskDialog = true
                },
                modifier = Modifier
                    .padding(12.dp)
                    .navigationBarsPadding()
                    .shadow(4.dp, CircleShape),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 6.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Convert Task",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) {
        innerPadding ->
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MoveToInbox,
                        contentDescription = "No task",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "There is no conversion task",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                items(
                    items = tasks,
                    key = { it.id },
                ) { task->
                    TaskItem(
                        task = task,
                        isEnableConvert = !isBatchRunning.value,
                        onStart = {
                            if (runningTaskId.value == null) {
                                runningTaskId.value = task.id
                                CoroutineScope(Dispatchers.IO).launch {
                                    task.startTask()
                                    withContext(Dispatchers.Main) {
                                        runningTaskId.value = null
                                    }
                                }
                            }
                        }
                    )
                    if (task != tasks.lastOrNull()) {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConvertMainPagePreview() {
    ComPDFKitConversionDemoTheme {
        ConvertMainPage()
    }
}