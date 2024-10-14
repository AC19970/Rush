package com.shub39.rush.database

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

object SettingsDataStore {

    private const val TAG = "SettingsDataStore"
    private const val DATA_STORE_FILE_NAME = "settings.pb"
    private val Context.dataStore by preferencesDataStore( name = DATA_STORE_FILE_NAME )

    private val MAX_LINES = intPreferencesKey("max_lines")
    private val TOGGLE_THEME = stringPreferencesKey("toggle_theme")
    private val SORT_ORDER = stringPreferencesKey("sort_order")
    private val CARD_COLOR = stringPreferencesKey("card_color")
    private val CARD_ROUNDNESS = stringPreferencesKey("card_roundness")
    private val CARD_THEME = stringPreferencesKey("card_theme")
    private val CARD_BACKGROUND = intPreferencesKey("card_background")
    private val CARD_CONTENT = intPreferencesKey("card_content")
    private val LYRICS_COLOR = stringPreferencesKey("lyrics_color")
    private val LARGE_CARD = booleanPreferencesKey("large_card")

    fun getLyricsColorFlow(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[LYRICS_COLOR] ?: "muted"
        }

    fun getLargeCardFlow(context: Context): Flow<Boolean> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[LARGE_CARD] ?: false
        }

    fun getCardBackgroundFlow(context: Context): Flow<Int> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[CARD_BACKGROUND] ?: Color.Black.toArgb()
        }

    fun getCardContentFlow(context: Context): Flow<Int> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[CARD_CONTENT] ?: Color.White.toArgb()
        }

    fun getCardThemeFlow(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[CARD_THEME] ?: "Spotify"
        }

    fun getCardColorFlow(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[CARD_COLOR] ?: "Vibrant"
        }

    fun getCardRoundnessFlow(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[CARD_ROUNDNESS] ?: "Rounded"
        }

    fun getSortOrderFlow(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[SORT_ORDER] ?: "title_asc"
        }

    fun getToggleThemeFlow(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[TOGGLE_THEME] ?: "Yellow"
        }

    fun getMaxLinesFlow(context: Context): Flow<Int> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[MAX_LINES] ?: 6
        }

    suspend fun updateCardBackground(context: Context, newCardBackground: Int) {
        context.dataStore.edit { settings ->
            settings[CARD_BACKGROUND] = newCardBackground
        }
    }

    suspend fun updateCardContent(context: Context, newCardContent: Int) {
        context.dataStore.edit { settings ->
            settings[CARD_CONTENT] = newCardContent
        }
    }

    suspend fun setLargeCard(context: Context, newLargeCard: Boolean) {
        context.dataStore.edit { settings ->
            settings[LARGE_CARD] = newLargeCard
        }
    }

    suspend fun setLyricsColor(context: Context, new: String) {
        context.dataStore.edit { settings ->
            settings[LYRICS_COLOR] = new
        }
    }

    suspend fun updateSortOrder(context: Context, newSortOrder: String) {
        context.dataStore.edit { settings ->
            settings[SORT_ORDER] = newSortOrder
        }
    }

    suspend fun updateCardTheme(context: Context, newCardTheme: String) {
        context.dataStore.edit { settings ->
            settings[CARD_THEME] = newCardTheme
        }
    }

    suspend fun updateCardColor(context: Context, newCardColor: String) {
        context.dataStore.edit { settings ->
            settings[CARD_COLOR] = newCardColor
        }
    }

    suspend fun updateCardRoundness(context: Context, newCardRoundness: String) {
        context.dataStore.edit { settings ->
            settings[CARD_ROUNDNESS] = newCardRoundness
        }
    }

    suspend fun updateMaxLines(context: Context, newMaxLines: Int) {
        context.dataStore.edit { settings ->
            settings[MAX_LINES] = newMaxLines
        }
    }

    suspend fun updateToggleTheme(context: Context, newToggleTheme: String) {
        context.dataStore.edit { settings ->
            settings[TOGGLE_THEME] = newToggleTheme
        }
    }
}