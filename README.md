# App Level Gradle

```kotlin
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-android-extensions'
    id 'kotlin-kapt'
}

android {
    compileSdkVersion 30
    buildToolsVersion "30.0.2"

    defaultConfig {
        applicationId "com.paigesoftware.roomdb_paginglibrary3"
        minSdkVersion 26
        targetSdkVersion 30
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
    //Data Binding
    buildFeatures {
        dataBinding true
    }
}

dependencies {

    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    implementation 'androidx.core:core-ktx:1.3.2'
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'com.google.android.material:material:1.2.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.2.0'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0'
    testImplementation 'junit:junit:4.+'
    androidTestImplementation 'androidx.test.ext:junit:1.1.2'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0'

    def room_version = "2.3.0-alpha04"
    def lifecycle_version = "2.2.0"
    def paging_version = "3.0.0-alpha12"

    implementation "androidx.paging:paging-runtime-ktx:$paging_version"
    testImplementation "androidx.paging:paging-common-ktx:$paging_version"

    // Room
    implementation "androidx.room:room-runtime:$room_version"
    implementation "androidx.room:room-ktx:$room_version"
    kapt "androidx.room:room-compiler:$room_version"

    // ViewModel & LiveData & lifecycle
    implementation "androidx.lifecycle:lifecycle-extensions:$lifecycle_version"
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
    implementation "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version"
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version"

    // Annotation processor for ViewModel & LiveData & lifecycle
    //kapt "androidx.lifecycle:lifecycle-compiler:$lifecycle_version"
    implementation "androidx.lifecycle:lifecycle-common-java8:$lifecycle_version"
}
```

# Main Activity

- it just hosts `Fragment`

```kotlin
package com.paigesoftware.roomdb_paginglibrary3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.paigesoftware.roomdb_paginglibrary3.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
    }
}
```

# MainFragment

```kotlin
package com.paigesoftware.roomdb_paginglibrary3.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.paigesoftware.roomdb_paginglibrary3.AppDatabase
import com.paigesoftware.roomdb_paginglibrary3.ListAdapter
import com.paigesoftware.roomdb_paginglibrary3.R
import com.paigesoftware.roomdb_paginglibrary3.StoredObject
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private lateinit var adapter: ListAdapter

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prePopDB()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        adapter = ListAdapter()
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        
        //get paging data 
        lifecycleScope.launch {
//            @OptIn(ExperimentalCoroutinesApi::class)
            viewModel.items.collectLatest {
                adapter.submitData(it)
            }
        }

    }

    //populate demo data 
    fun prePopDB() {
        val dao = Room.databaseBuilder(context!!, AppDatabase::class.java, "myDb")
                .build()
                .storedObjectDao()
        GlobalScope.launch {
            for(i in 0..50) {
                val result = dao.insert(StoredObject(_id=0, name="name$i"))
                Log.d("MainActivity", "Result: $result")
            }
        }

    }

}
```

# MainViewModel

```kotlin
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

		//paging data
    val items = Pager(
        PagingConfig(
            pageSize = 1,
            enablePlaceholders = false
        )
    ) {
        dao.getAllPaged()
    }.flow

}
```

# PagingDataAdapter

- You should implement `PagingDataAdapter` in order to use Paging3 library

```kotlin
package com.paigesoftware.roomdb_paginglibrary3

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ListAdapter : PagingDataAdapter<StoredObject, ListAdapter.ListViewHolder>(DIFF_CALLBACK) {

    private val LOG_TAG = "listadapter"

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoredObject>() {
            init {
                Log.d("ListAdapter", "DIFF UTIL created")
            }

            override fun areContentsTheSame(oldItem: StoredObject, newItem: StoredObject): Boolean {
                return oldItem._id == newItem._id
            }

            override fun areItemsTheSame(oldItem: StoredObject, newItem: StoredObject): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val storedObject = getItem(position)
        holder.bindTo(storedObject)
    }

    class ListViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
    ) {
        private val nameView = itemView.findViewById<TextView>(R.id.name)
        var storedObject: StoredObject? = null

        fun bindTo(storedObject: StoredObject?) {
            this.storedObject = storedObject
            nameView.text = storedObject?.name
        }

    }

}
```

# AppDatabase & Entity & Dao

```kotlin
// Database
package com.paigesoftware.roomdb_paginglibrary3

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(StoredObject::class), version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun storedObjectDao(): StoredObjectDao
}

// Entity
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

// Dao
package com.paigesoftware.roomdb_paginglibrary3

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface StoredObjectDao {
		
		/* Dao which returns `PagingSource` */
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
```