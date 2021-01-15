package com.paigesoftware.roomdb_paginglibrary3

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface StoredObjectDao {

    @Query("SELECT * FROM storedobject ORDER BY _id DESC")
    fun getAllPaged(): PagingSource<Int, StoredObject>

//    @Query("SELECT * FROM storedobject ORDER BY uni")

    @Update
    suspend fun update(item: StoredObject): Int

    @Insert
    suspend fun insert(item: StoredObject): Long

    @Delete
    suspend fun delete(item: StoredObject): Int

}