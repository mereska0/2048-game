package com.alexbyr.game2048.game.presentation

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

interface BestScoreStore {
    val bestScore: Flow<Int>
    suspend fun saveBestScore(score: Int)
}

private val Context.dataStore by preferencesDataStore(name = "game2048_prefs")

class PreferencesBestScoreStore(
    private val context: Context,
) : BestScoreStore {
    override val bestScore: Flow<Int> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[BEST_SCORE_KEY] ?: 0
            }

    override suspend fun saveBestScore(score: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[BEST_SCORE_KEY] ?: 0
            if (score > current) {
                preferences[BEST_SCORE_KEY] = score
            }
        }
    }

    private companion object {
        val BEST_SCORE_KEY = intPreferencesKey("best_score")
    }
}
