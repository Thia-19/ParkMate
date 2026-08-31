package com.example.parkmate.feature_savedLocations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parkmate.feature_savedLocations.components.CreateLocationBottomSheet
import com.example.parkmate.feature_savedLocations.components.SavedLocationContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedLocationsScreen(
    viewModel: SavedLocationsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onEvent: (SavedLocationsEvent) -> Unit,
){
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isModalVisible = state.isCreateModalOpen || state.locationToEdit != null
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect (state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Saved Locations",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { onEvent(SavedLocationsEvent.OpenCreateModal)}) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) { padding ->

        if(state.locations.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.40f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.5f))
            )
        }

        SavedLocationContent(
            locations = state.locations,
            isLoading = state.isLoading,
            onEvent = onEvent,
            modifier = Modifier.padding(padding)
        )

        if (isModalVisible) {
            CreateLocationBottomSheet (
                locationToEdit = state.locationToEdit,
                onDismiss = {
                    onEvent(SavedLocationsEvent.DismissCreateModal)
                },
                onSave = { name, icon, position ->
                    val locationToEdit = state.locationToEdit
                    if (locationToEdit != null) {
                        onEvent(
                            SavedLocationsEvent.UpdateSavedLocation(
                                id = locationToEdit.id,
                                name = name,
                                icon = icon,
                                position = position
                            )
                        )
                    } else {
                        onEvent(
                            SavedLocationsEvent.AddSavedLocation(
                                name = name,
                                icon = icon,
                                position = position
                            )
                        )
                    }
                }
            )
        }
    }
}

