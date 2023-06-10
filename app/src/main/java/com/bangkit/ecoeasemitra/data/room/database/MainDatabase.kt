package com.bangkit.ecoeasemitra.data.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bangkit.ecoeasemitra.data.room.dao.*
import com.bangkit.ecoeasemitra.data.room.model.*

@Database(
    entities =
    [
        (Address::class),
        (ChatRoom::class),
        (Message::class),
        (Garbage::class),
        (User::class),
        (Order::class),
        (DetailTransaction::class),
        (Mitra::class),
        (Location::class),
   ],
    version = 7,
    exportSchema = false
)
abstract class MainDatabase : RoomDatabase(){
    abstract fun addressDao(): AddressDao
    abstract fun chatRoomDao(): ChatRoomDao
    abstract fun messageDao(): MessageDao
    abstract fun garbageDao(): GarbageDao
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao
    abstract fun detailTransactionDao(): DetailTransactionDao
    abstract fun mitraDao(): MitraDao
    abstract fun locationDao(): LocationDao

    companion object{
        @Volatile
        private var INSTANCE: MainDatabase? = null

        fun getInstance(context: Context): MainDatabase{
            if(INSTANCE == null) synchronized(MainDatabase::class.java){
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                    MainDatabase::class.java,
                    "ecoease_database"
                ).fallbackToDestructiveMigration().build()
            }

            return INSTANCE as MainDatabase
        }
    }
}