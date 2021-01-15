package com.paigesoftware.roomdb_paginglibrary3

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StoredObject (
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    @ColumnInfo(name = "name")
    val name: String
)