package com.example.nutritrack.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nutritrack.util.Converters
import java.io.File

@Database(
    entities = [
        LogsEntity::class,
        FoodEntity::class,
        FavoritesEntity::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logsDAO(): LogsDAO
    abstract fun foodDAO(): FoodDAO
    abstract fun favoritesDAO(): FavoritesDAO

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "NutriTrackDatabase"
            )
            .fallbackToDestructiveMigration()
            .build()
        }
    }
}
