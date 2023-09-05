package com.example.simplesearch.db

import android.content.Context
import androidx.room.*
import com.example.simplesearch.data.Item
import com.example.simplesearch.data.OwnerTypeConverter


@Database(
    entities = [Item::class, RemoteKeys::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(OwnerTypeConverter::class)
abstract class ReposDataBase: RoomDatabase() {

    abstract fun reposDao(): ReposDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {

        @Volatile
        private var INSTANCE: ReposDataBase? = null

        fun getInstance(context: Context): ReposDataBase =
            INSTANCE?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                ReposDataBase::class.java,
                "Github.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}