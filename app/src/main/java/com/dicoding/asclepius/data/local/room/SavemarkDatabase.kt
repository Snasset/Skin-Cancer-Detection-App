package com.dicoding.asclepius.data.local.room


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.asclepius.data.local.entity.SavemarkEntity


@Database(entities = [SavemarkEntity::class], version = 1, exportSchema = false)
abstract class SavemarkDatabase : RoomDatabase() {
    abstract fun savemarkDao(): SavemarkDao

    companion object {
        @Volatile
        private var instance: SavemarkDatabase? = null
        fun getInstance(context: Context): SavemarkDatabase {
            if (instance == null) {
                synchronized(SavemarkDatabase::class.java) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SavemarkDatabase::class.java, "savemark_db"
                    )
                        .build()
                }
            }
            return instance as SavemarkDatabase
        }
    }

}