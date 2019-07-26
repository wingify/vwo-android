package com.vwo.sampleapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.vwo.sampleapp.models.Mobile

/**
 * Created by aman on Wed 01/08/18 18:04.
 */
class MobileDiffUtils(private val oldMobiles: List<Mobile>?, private val newMobiles: List<Mobile>?): DiffUtil.Callback() {

    /**
     * Called by the DiffUtil to decide whether two object represent the same Item.
     *
     *
     * For example, if your items have unique ids, this method should check their id equality.
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list
     * @return True if the two items represent the same object or false if they are different.
     */
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldMobiles?.get(oldItemPosition)
        val newItem = newMobiles?.get(newItemPosition)
        if (oldItem != null && newItem != null) {
            return oldItem.id == newItem.id
        }

        return false
    }

    /**
     * Called by the DiffUtil when it wants to check whether two items have the same data.
     * DiffUtil uses this information to detect if the contents of an item has changed.
     *
     *
     * DiffUtil uses this method to check equality instead of [Object.equals]
     * so that you can change its behavior depending on your UI.
     * For example, if you are using DiffUtil with a
     * [RecyclerView.Adapter], you should
     * return whether the items' visual representations are the same.
     *
     *
     * This method is called only if [.areItemsTheSame] returns
     * `true` for these items.
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list which replaces the
     * oldItem
     * @return True if the contents of the items are the same or false if they are different.
     */
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldMobiles?.get(oldItemPosition)
        val newItem = newMobiles?.get(newItemPosition)
        if (oldItem != null && newItem != null) {
            return ((oldItem.name == newItem.name) && (oldItem.vendor == newItem.vendor) && (oldItem.id == newItem.id))
        }
        return false
    }

    /**
     * Returns the size of the old list.
     *
     * @return The size of the old list.
     */
    override fun getOldListSize(): Int {
        return oldMobiles?.size ?: 0
    }

    /**
     * Returns the size of the new list.
     *
     * @return The size of the new list.
     */
    override fun getNewListSize(): Int {
        return newMobiles?.size ?: 0
    }
}