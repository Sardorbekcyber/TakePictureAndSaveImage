package com.turbosoft.takepictureandsaveimage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UriDao {

    @Query("SELECT * FROM content_uri")
    fun getAllUris() : List<ContentUri>

    @Insert
    fun insertContentUri(contentUri: ContentUri) : Long


}