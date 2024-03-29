package com.example.ivcareapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class],version = 1)
abstract class UserDatabase : RoomDatabase() {

    abstract val userDAO : UserDAO

    companion object{
        @Volatile
        private var INSTANCE : UserDatabase? = null
        fun getInstance(context: Context):UserDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance==null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        "user_data_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
