package com.compdfkit.conversion.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.compdfkit.conversion.entity.ConversionTask

class MainViewModel : ViewModel() {
    var runningTaskId = mutableStateOf<String?>(null)
        private set

    var isBatchRunning = mutableStateOf(false)
        private set

    var isShowNewTaskDialog = mutableStateOf(false)
        private set

    val tasks = mutableStateListOf<ConversionTask>()

    fun setRunningTaskId(id: String?) {
        runningTaskId.value = id
    }

    fun setBatchRunning(running: Boolean) {
        isBatchRunning.value = running
    }

    fun showNewTaskDialog(show: Boolean) {
        isShowNewTaskDialog.value = show
    }

    fun addTask(task: ConversionTask) {
        tasks.add(task)
    }
}