package com.example.parkmate.feature_vehiclesManager.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

    var isGridView by remember {
        mutableStateOf(false)
    }

    var showEditDialog by remember {
        mutableStateOf(false)
    }

    var vehicleToEdit by remember {
        mutableStateOf<Vehicle?>(null)
    }

    val searchBarState = rememberSearchBarState()

    // Needed to call suspend animation functions
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

                                    onQueryChange = { query ->
                                        viewModel.updateSearchQuery(query)
                                    },

                                    onSearch = {
                                        coroutineScope.launch {
                                            searchBarState.animateToCollapsed()
                                        }
                                    },

                                    expanded =
                                        searchBarState.currentValue ==
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
                                            contentDescription = "Search"
                                        )
                                    }
                                )
                            },

                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )

                        IconButton(
                            onClick = {
                                // TODO: Open filters
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterAlt,
                                contentDescription = "Filter",
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                isGridView = !isGridView
                            }
                        ) {
                            Icon(
                                imageVector =
                                    if (isGridView) {
                                        Icons.Default.ViewList
                                    } else {
                                        Icons.Default.GridView
                                    },

                                contentDescription = "Toggle View",

                                modifier = Modifier.size(32.dp)
                            )
                        }
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

                containerColor =
                    MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Vehicle"
                )
            }
        }

    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            VehiclesList(
                vehicles = vehiclesList,

                isGridView = isGridView,

                onEditVehicle = { parkedVehicle ->
                    vehicleToEdit = parkedVehicle.vehicle
                    showEditDialog = true
                },

                onDeleteVehicle = { parkedVehicle ->
                    viewModel.deleteVehicle(
                        parkedVehicle.vehicle
                    )
                },

                modifier = Modifier.fillMaxSize()
            )

            if (showEditDialog) {

                EditVehicleScreen(

                    vehicleToEdit = vehicleToEdit,

                    onDismiss = {
                        showEditDialog = false
                        vehicleToEdit = null
                    },

                    onSave = { name, type, imageUriString ->

                        val vehicleToSave =
                            vehicleToEdit?.copy(
                                name = name,
                                type = type
                            )
                                ?: Vehicle(
                                    name = name,
                                    type = type
                                )

                        val uri = imageUriString?.toUri()

                        viewModel.saveVehicle(
                            vehicleToSave,
                            uri
                        )

                        showEditDialog = false
                        vehicleToEdit = null
                    }
                )
            }
        }
    }
}