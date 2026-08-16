package com.example.parkmate.core.preferences.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")
class SettingsDataStore @Inject constructor(
    private val context: Context
) {
    private object PreferencesKeys {
        val USE_CURRENT_POSITION = booleanPreferencesKey("use_current_position")
    }

    val useCurrentPositionFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException){
                emit (emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.USE_CURRENT_POSITION] ?: true
        }

    suspend fun setUseCurrentPosition(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_CURRENT_POSITION] = enabled
        }
    }
}