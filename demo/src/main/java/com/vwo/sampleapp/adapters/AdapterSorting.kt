package com.vwo.sampleapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vwo.sampleapp.R
import com.vwo.sampleapp.fragments.FragmentSortingMain
import com.vwo.sampleapp.interfaces.ItemClickListener
import com.vwo.sampleapp.models.Mobile
import com.vwo.sampleapp.utils.MobileDiffUtils

/**
 * Created by aman on 07/08/17.
 */

class AdapterSorting(mobiles: MutableList<Mobile>?, private val mContext: Context, @param:FragmentSortingMain.FragmentType @field:FragmentSortingMain.FragmentType
private val type: Int, private val itemClickListener: ItemClickListener?) : RecyclerView.Adapter<AdapterSorting.ViewHolderList>() {
    private val mobiles: MutableList<Mobile> = mutableListOf()

    init {
        mobiles?.let { this.mobiles.addAll(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterSorting.ViewHolderList {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_item_sorting_list, parent, false)
        return ViewHolderList(view)
    }

    override fun onBindViewHolder(holder: AdapterSorting.ViewHolderList, position: Int) {

        holder.itemName.text = mobiles!![position].name
        holder.itemImage.setImageResource(mobiles[position].imageId)
        holder.itemPrice.text = String.format(mContext.getString(R.string.price_format), mobiles[position].units, mobiles[position].price)
        holder.itemVendor.text = mContext.resources.getString(R.string.vendor_name, mobiles[position].vendor)
    }

    fun getItemAt(position: Int): Mobile? {
        return if (mobiles.size > position) mobiles[position] else null
    }

    override fun getItemCount(): Int {
        return mobiles?.size ?: 0
    }

    fun updateData(newMobiles: List<Mobile>) {
        val diffResult = DiffUtil.calculateDiff(MobileDiffUtils(mobiles, newMobiles))
        this.mobiles.clear()
        this.mobiles.addAll(newMobiles)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolderList(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: AppCompatImageView = itemView.findViewById(R.id.item_image)
        val itemName: AppCompatTextView = itemView.findViewById(R.id.item_name)
        val itemVendor: AppCompatTextView = itemView.findViewById(R.id.item_vendor)
        val itemPrice: AppCompatTextView = itemView.findViewById(R.id.item_price)

        init {
            itemView.setOnClickListener {
                itemClickListener?.onItemClicked(this@ViewHolderList, adapterPosition)
            }
        }
    }
}
