package com.example.parkmate.feature_history.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.Position
import androidx.compose.material3.Surface

// UI Composable principale

@Composable
fun HistoryScreen (
    viewModel: HistoryViewModel = hiltViewModel()
){
    val state by viewModel.state.collectAsStateWithLifecycle()
    HistoryContent(state = state, onEvent = viewModel::onEvent)
    // ...
    // Nella UI Compose (quando l'utente clicca elimina):
    //
    //Kotlin
    //// Passi l'oggetto sessione direttamente alla callback dell'evento
    //onEvent(HistoryEvent.DeleteSession(session))
}


// 2. Componente di sola UI (Stateless) -> Perfetto per le Preview!
@Composable
fun HistoryContent(
    state: HistoryState,
    onEvent: (HistoryEvent) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Your parking sessions (${state.parkingSessions.size})",
                style = MaterialTheme.typography.headlineMedium
            )

            // Qui disegni la tua lista o la tua UI vera e propria...
        }
    }
}

@Preview(showBackground = true, name = "Stato con dati di esempio")
@Composable
fun HistoryContentPreview() {
    // Creiamo uno stato fittizio (Mock) con dati di prova per la Preview
    val dummyState = HistoryState(
        parkingSessions = listOf(
            ParkingSession(
                id = 1,
                isActive = false,
                vehicleId = 101,
                parkingType = ParkingType.HOURLY_PAID,
                startTime = System.currentTimeMillis() - 7200000, // 2 ore fa
                endTime = System.currentTimeMillis(),
                position = Position(44.4949, 11.3426),
                hourlyRate = 1.50,
                finalCost = 3.00
            ),
            ParkingSession(
                id = 2,
                isActive = false,
                vehicleId = 102,
                parkingType = ParkingType.FIXED_TICKET,
                startTime = System.currentTimeMillis() - 86400000,
                endTime = System.currentTimeMillis() - 80000000,
                position = Position(44.5000, 11.3500),
                fixedCost = 5.00,
                finalCost = 5.00
            )
        ),
        isLoading = false
    )

    // Passiamo lo stato fittizio e una lambda vuota per gli eventi
    HistoryContent(
        state = dummyState,
        onEvent = {}
    )
}