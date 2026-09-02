package com.example.parkmate.feature_parkingEffort.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.BackgroundTaskManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteTrackingViewModel @Inject constructor(
    private val backgroundTaskManager: BackgroundTaskManager
) : ViewModel() {

    private val _events = MutableSharedFlow<RouteTrackingEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events = _events.asSharedFlow()

    fun startRouteTracking(sessionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                backgroundTaskManager.startRouteTracking(sessionId)
            }.onSuccess {
                _events.tryEmit(RouteTrackingEvent.Started)
            }.onFailure { error ->
                _events.tryEmit(RouteTrackingEvent.FailedToStart(error))
            }
        }
    }

    fun stopRouteTracking(sessionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { backgroundTaskManager.stopRouteTracking(sessionId) }
        }
    }

    fun markAsArrived(sessionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { backgroundTaskManager.markAsArrived(sessionId) }
        }
    }
}

sealed interface RouteTrackingEvent {
    data object Started : RouteTrackingEvent
    data class FailedToStart(val error: Throwable?) : RouteTrackingEvent
}

