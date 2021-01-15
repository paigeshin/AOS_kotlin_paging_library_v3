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