package com.paigesoftware.roomdb_paginglibrary3

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(StoredObject::class), version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun storedObjectDao(): StoredObjectDao
}