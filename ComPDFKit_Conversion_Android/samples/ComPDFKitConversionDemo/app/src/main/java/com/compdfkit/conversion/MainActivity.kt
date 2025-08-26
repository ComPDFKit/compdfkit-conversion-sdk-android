package com.compdfkit.conversion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.compdfkit.conversion.component.ConvertMainPage
import com.compdfkit.conversion.ui.theme.ComPDFKitConversionDemoTheme
import com.compdfkit.conversion.utils.AppContextHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        lifecycleScope.launch {
//            withContext(Dispatchers.IO) {
//                ConversionHelper.installAIModel(this@MainActivity)
//            }
//        }
        AppContextHolder.init(this)
        enableEdgeToEdge()
        setContent {
            ComPDFKitConversionDemoTheme {
                ConvertMainPage()
            }
        }
    }
}