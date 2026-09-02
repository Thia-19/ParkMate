package com.example.parkmate.feature_vehiclesManager.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Column
import com.example.parkmate.feature_vehiclesManager.components.VehicleFilters
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parkmate.core.domain.models.vehicle.Vehicle
import com.example.parkmate.feature_vehiclesManager.components.VehiclesList
import com.example.parkmate.feature_vehiclesManager.viewModel.VehiclesManagerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayVehiclesScreen(
    viewModel: VehiclesManagerViewModel = hiltViewModel()
) {
    val vehiclesList by viewModel.vehicles.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()

    var showEditDialog by remember {
        mutableStateOf(false)
    }

    var vehicleToEdit by remember {
        mutableStateOf<Vehicle?>(null)
    }

    val searchBarState = rememberSearchBarState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SearchBar(
                            state = searchBarState,
                            inputField = {
                                SearchBarDefaults.InputField(
                                    query = searchQuery,
                                    onQueryChange = viewModel::updateSearchQuery,
                                    onSearch = {
                                        coroutineScope.launch {
                                            searchBarState.animateToCollapsed()
                                        }
                                    },
                                    expanded = searchBarState.currentValue ==
                                            SearchBarValue.Expanded,
                                    onExpandedChange = { isExpanded ->
                                        coroutineScope.launch {
                                            if (isExpanded) {
                                                searchBarState.animateToExpanded()
                                            } else {
                                                searchBarState.animateToCollapsed()
                                            }
                                        }
                                    },
                                    placeholder = {
                                        Text("Cerca...")
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Cerca veicoli"
                                        )
                                    }
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                    }
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

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                VehicleFilters(
                    filterState = filterState,
                    onStatusFilterSelected = viewModel::setStatusFilter,
                    onTypeFilterSelected = viewModel::setTypeFilter
                )

                VehiclesList(
                    vehicles = vehiclesList,
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
                EditVehicleScreen(
                    vehicleToEdit = vehicleToEdit,
                    onDismiss = {
                        showEditDialog = false
                        vehicleToEdit = null
                    },
                    onSave = { name, type, imageUriString ->

                        val vehicleToSave = vehicleToEdit?.copy(
                            name = name,
                            type = type
                        ) ?: Vehicle(
                            name = name,
                            type = type
                        )

                        viewModel.saveVehicle(
                            vehicle = vehicleToSave,
                            imageUri = imageUriString?.toUri()
                        )

                        showEditDialog = false
                        vehicleToEdit = null
                    }
                )
            }
        }
    }
}