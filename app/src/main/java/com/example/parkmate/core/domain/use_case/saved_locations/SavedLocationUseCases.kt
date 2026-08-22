package com.example.parkmate.core.domain.use_case.saved_locations

import javax.inject.Inject

data class SavedLocationUseCases @Inject constructor(
    val getAllSavedLocations: GetAllSavedLocations,
    val addSavedLocation: AddSavedLocation,
    val deleteSavedLocation: DeleteSavedLocation,
    val updateSavedLocation: UpdateSavedLocation
)