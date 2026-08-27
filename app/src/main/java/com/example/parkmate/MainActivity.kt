package com.example.parkmate

import com.example.parkmate.feature_home.HomeScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.parkmate.ui.MainScreen
import com.example.parkmate.ui.theme.ParkMateTheme // Aggiusta il package del tuo tema se diverso
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.MapsInitializer.Renderer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST) { renderer ->
                        when (renderer) {
                            MapsInitializer.Renderer.LATEST -> Log.d("MapsApp", "Google Maps Renderer caricato: LATEST")
                            MapsInitializer.Renderer.LEGACY -> Log.d("MapsApp", "Google Maps Renderer caricato: LEGACY")
                        }
                    }
                }
            }

            ParkMateTheme {
                MainScreen()
            }
        }
    }
}
