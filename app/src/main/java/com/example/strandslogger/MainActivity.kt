package com.example.strandslogger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.strandslogger.data.local.AppDatabase
import com.example.strandslogger.navigation.AppHost
import com.example.strandslogger.ui.theme.StrandsLoggerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StrandsLoggerTheme {
                AppHost(applicationContext)
            }
        }
    }
}