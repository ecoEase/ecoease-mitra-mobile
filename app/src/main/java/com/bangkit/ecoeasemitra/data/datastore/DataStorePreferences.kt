package com.bangkit.ecoeasemitra.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bangkit.ecoeasemitra.config.datastore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStorePreferences(private val context: Context) {
    private val READ_ONBOARD = booleanPreferencesKey("read_onboard")
    private val AUTH_TOKEN = stringPreferencesKey("auth_token")
    private val FCM_TOKEN = stringPreferencesKey("fcm_token")

    fun isFinishReadOnBoard(): Flow<Boolean> {
        return context.datastore.data.map {
            it[READ_ONBOARD] ?: false
        }
    }
    fun getAuthToken(): Flow<String> = context.datastore.data.map { it[AUTH_TOKEN] ?: "" }
    fun getFCMToken(): Flow<String> = context.datastore.data.map { it[FCM_TOKEN] ?: "" }
    suspend fun finishReadOnboard() {
        context.datastore.edit {
            it[READ_ONBOARD] = true
        }
    }
    suspend fun setToken(newToken: String) {
        context.datastore.edit {
            it[AUTH_TOKEN] = newToken
        }
    }
    suspend fun setFCMToken(newToken: String) {
        context.datastore.edit {
            it[FCM_TOKEN] = newToken
        }
    }

    companion object {
        @Volatile
        var INSTANCE: DataStorePreferences? = null

        fun getInstances(context: Context): DataStorePreferences = INSTANCE ?: synchronized(this) {
            val instance = DataStorePreferences(context)
            INSTANCE = instance
            instance
        }
    }
}