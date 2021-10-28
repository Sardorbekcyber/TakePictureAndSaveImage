package com.turbosoft.takepictureandsaveimage

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [ContentUri::class],
    version = 1,
    exportSchema = false
)
abstract class UriDatabase : RoomDatabase() {

    abstract fun uriDao(): UriDao

}