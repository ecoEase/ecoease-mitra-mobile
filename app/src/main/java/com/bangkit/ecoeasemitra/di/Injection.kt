package com.bangkit.ecoeasemitra.di

import android.content.Context
import com.bangkit.ecoeasemitra.data.datastore.DataStorePreferences
import com.bangkit.ecoeasemitra.data.repository.MainRepository
import com.bangkit.ecoeasemitra.data.room.database.MainDatabase

object Injection {
    fun provideInjection(context: Context): MainRepository{
        val datastore = DataStorePreferences.getInstances(context)
        val roomDatabase = MainDatabase.getInstance(context)
        return MainRepository.getInstance(datastore, roomDatabase, context)
    }
}