package com.example.parkmate.feature_parkingEffort.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.BackgroundTaskManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RouteTrackingEvent {
    data object Started : RouteTrackingEvent
    data class FailedToStart(val error: Throwable) : RouteTrackingEvent
    data class OperationFailed(val error: Throwable) : RouteTrackingEvent
}

@HiltViewModel
class RouteTrackingViewModel @Inject constructor(
    private val backgroundTaskManager: BackgroundTaskManager
) : ViewModel() {

    private val _events = Channel<RouteTrackingEvent>(capacity = Channel.BUFFERED)
    val events: Flow<RouteTrackingEvent> = _events.receiveAsFlow()

    fun startRouteTracking(sessionId: Int) {
        viewModelScope.launch {
            runCatching {
                backgroundTaskManager.startRouteTracking(sessionId)
            }.onSuccess {
                _events.send(RouteTrackingEvent.Started)
            }.onFailure { error ->
                _events.send(RouteTrackingEvent.FailedToStart(error))
            }
        }
    }
}
