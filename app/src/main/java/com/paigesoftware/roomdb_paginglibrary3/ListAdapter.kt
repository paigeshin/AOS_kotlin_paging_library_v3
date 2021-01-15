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