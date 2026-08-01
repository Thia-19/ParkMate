package com.example.parkmate.feature_history.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

// UI Composable principale

@Composable
fun HistoryScreen (
    viewModel: HistoryViewModel = hiltViewModel()
){
    // val state by viewModel.state.collectAsStateWithLifecycle()

    // ...
    // Nella UI Compose (quando l'utente clicca elimina):
    //
    //Kotlin
    //// Passi l'oggetto sessione direttamente alla callback dell'evento
    //onEvent(HistoryEvent.DeleteSession(session))
}