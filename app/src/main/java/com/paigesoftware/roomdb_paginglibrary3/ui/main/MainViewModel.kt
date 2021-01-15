package com.paigesoftware.roomdb_paginglibrary3.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import androidx.room.RoomDatabase
import com.paigesoftware.roomdb_paginglibrary3.AppDatabase

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val dao = Room.databaseBuilder(application, AppDatabase::class.java, "myDb")
        .build()
        .storedObjectDao()

//    PagingConfig(
//    pageSize = 50,
//    enablePlaceholders = false,
//    maxSize = 200
//    )

    val items = Pager(
        PagingConfig(
            pageSize = 1,
            enablePlaceholders = false
        )
    ) {
        dao.getAllPaged()
    }.flow

}