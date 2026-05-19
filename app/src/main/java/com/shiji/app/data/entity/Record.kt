package com.shiji.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "bristol_type")
    val bristolType: Int,

    @ColumnInfo(name = "color")
    val color: String,

    @ColumnInfo(name = "odor_level")
    val odorLevel: Int,

    @ColumnInfo(name = "smoothness")
    val smoothness: Int,

    @ColumnInfo(name = "commentary")
    val commentary: String,

    @ColumnInfo(name = "note")
    val note: String? = null
)
