package com.example.parkmate

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import com.example.compose.ParkMateTheme
import com.example.parkmate.ui.MainScreen
import com.google.android.gms.maps.MapsInitializer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var pendingNavigationAction = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        pendingNavigationAction.value = intent?.getStringExtra("navigate_to")

        setContent {
            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST) {}
                }
            }

            ParkMateTheme {
                MainScreen(
                    pendingNavigationAction = pendingNavigationAction.value,
                    onNavigationHandled = { pendingNavigationAction.value = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingNavigationAction.value = intent.getStringExtra("navigate_to")
    }
}