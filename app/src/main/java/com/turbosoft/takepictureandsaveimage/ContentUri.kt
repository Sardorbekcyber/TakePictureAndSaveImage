package com.turbosoft.takepictureandsaveimage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "content_uri")
data class ContentUri(
    val uri: String,
    @PrimaryKey(autoGenerate = true)
    val uriId: Long = 0
)