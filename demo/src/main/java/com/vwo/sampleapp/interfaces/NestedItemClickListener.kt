package com.vwo.sampleapp.interfaces

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by aman on Wed 18/07/18 12:10.
 */
interface NestedItemClickListener {
    fun onItemClicked(viewHolder: RecyclerView.ViewHolder, parentAdapterPosition: Int, childAdapterPosition: Int)
}