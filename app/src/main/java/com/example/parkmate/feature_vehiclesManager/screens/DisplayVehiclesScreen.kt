package com.example.parkmate.feature_vehiclesManager.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.vehicle.ParkedVehicle
import com.example.parkmate.core.domain.models.vehicle.Vehicle
import com.example.parkmate.feature_vehiclesManager.components.VehicleFilters
import com.example.parkmate.feature_vehiclesManager.components.VehiclesList
import com.example.parkmate.feature_vehiclesManager.components.dialogs.EditVehicleDialog
import com.example.parkmate.feature_vehiclesManager.viewModel.VehicleUiEvent
import com.example.parkmate.feature_vehiclesManager.viewModel.VehiclesManagerViewModel
import com.example.parkmate.feature_vehiclesManager.viewModel.VehiclesUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayVehiclesScreen(
    viewModel: VehiclesManagerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()

    var showEditDialog by remember { mutableStateOf(false) }
    var vehicleToEdit by remember { mutableStateOf<Vehicle?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val vehicleDeletedMessage = stringResource(R.string.vehicle_deleted)

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is VehicleUiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(message = event.message)
                }

                is VehicleUiEvent.VehicleDeleted -> {
                    snackbarHostState.showSnackbar(message = vehicleDeletedMessage)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    VehicleSearchField(
                        query = searchQuery,
                        onQueryChange = viewModel::updateSearchQuery,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    vehicleToEdit = null
                    showEditDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Aggiungi veicolo"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                VehicleFilters(
                    filterState = filterState,
                    onStatusFilterSelected = viewModel::setStatusFilter,
                    onTypeFilterSelected = viewModel::setTypeFilter
                )

                VehiclesContent(
                    uiState = uiState,
                    onEditVehicle = { parkedVehicle ->
                        vehicleToEdit = parkedVehicle.vehicle
                        showEditDialog = true
                    },
                    onDeleteVehicle = { parkedVehicle ->
                        viewModel.deleteVehicle(parkedVehicle.vehicle)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            if (showEditDialog) {
                EditVehicleDialog(
                    vehicleToEdit = vehicleToEdit,
                    onDismiss = {
                        showEditDialog = false
                        vehicleToEdit = null
                    },
                    onSave = { name, type, imageUriString ->
                        val vehicleToSave = viewModel.buildVehicleToSave(
                            existingVehicle = vehicleToEdit,
                            name = name,
                            type = type
                        )

                        viewModel.saveVehicle(
                            vehicle = vehicleToSave,
                            imageUriString = imageUriString
                        )

                        showEditDialog = false
                        vehicleToEdit = null
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchBarState = rememberSearchBarState()
    val coroutineScope = rememberCoroutineScope()

    SearchBar(
        state = searchBarState,
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = {
                    coroutineScope.launch { searchBarState.animateToCollapsed() }
                },
                expanded = searchBarState.currentValue == SearchBarValue.Expanded,
                onExpandedChange = { isExpanded ->
                    coroutineScope.launch {
                        if (isExpanded) {
                            searchBarState.animateToExpanded()
                        } else {
                            searchBarState.animateToCollapsed()
                        }
                    }
                },
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Cerca")
                }
            )
        },
        modifier = modifier
    )
}

@Composable
private fun VehiclesContent(
    uiState: VehiclesUiState,
    onEditVehicle: (ParkedVehicle) -> Unit,
    onDeleteVehicle: (ParkedVehicle) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is VehiclesUiState.Loading -> {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is VehiclesUiState.Error -> {
            Box(modifier = modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        is VehiclesUiState.Success -> {
            VehiclesList(
                vehicles = uiState.vehicles,
                onEditVehicle = onEditVehicle,
                onDeleteVehicle = onDeleteVehicle,
                modifier = modifier
            )
        }
    }
}