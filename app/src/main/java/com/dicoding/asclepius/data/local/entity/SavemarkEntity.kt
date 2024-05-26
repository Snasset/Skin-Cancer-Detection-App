package com.dicoding.asclepius.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class SavemarkEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "label")
    var label: String,

    @ColumnInfo(name = "confidenceScore")
    var confidenceScore: String,

    @ColumnInfo(name = "imageResult")
    var imageResult: String,

    @ColumnInfo(name = "descResult")
    var descResult: String,
) : Parcelable