package com.turbosoft.takepictureandsaveimage

import android.content.Context
import androidx.room.Room

class DatabaseProvider(
    private val context: Context
) {

    fun provideDatabase(): UriDatabase = Room.databaseBuilder(
        context, UriDatabase::class.java, "uri.database.db"
    )
        .fallbackToDestructiveMigration()
        .build()


}