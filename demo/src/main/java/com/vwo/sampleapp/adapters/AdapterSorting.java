package com.vwo.sampleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vwo.sampleapp.R;
import com.vwo.sampleapp.fragments.FragmentSortingMain;
import com.vwo.sampleapp.interfaces.ItemClickListener;
import com.vwo.sampleapp.models.Mobile;

import java.util.ArrayList;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by aman on 07/08/17.
 */

public class AdapterSorting extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Mobile> mobiles;
    private Context mContext;

    @FragmentSortingMain.FragmentType
    private int type;
    private ItemClickListener itemClickListener;

    public AdapterSorting(ArrayList<Mobile> mobiles, Context context, @FragmentSortingMain.FragmentType int type, ItemClickListener itemClickListener) {
        this.mobiles = new ArrayList<>(mobiles);
        this.mContext = context;
        this.type = type;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(type == FragmentSortingMain.ID_LIST_VARIATION) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_sorting_list, parent, false);
            return new ViewHolderList(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_sorting_grid, parent, false);
            return new ViewHolderGrid(view);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if(viewHolder instanceof ViewHolderList) {

            ViewHolderList holder = (ViewHolderList) viewHolder;

            holder.itemName.setText(mobiles.get(position).getName());
            holder.itemImage.setImageResource(mobiles.get(position).getImageId());
            holder.itemPrice.setText(String.format(mContext.getString(R.string.price_format), mobiles.get(position).getUnits(), mobiles.get(position).getPrice()));
            holder.itemVendor.setText(mContext.getResources().getString(R.string.vendor_name, mobiles.get(position).getVendor()));

        } else if(viewHolder instanceof ViewHolderGrid) {

            ViewHolderGrid holder = (ViewHolderGrid) viewHolder;

            holder.itemName.setText(mobiles.get(position).getName());
            holder.itemImage.setImageResource(mobiles.get(position).getImageId());
            holder.itemPrice.setText(String.format(mContext.getString(R.string.price_format), mobiles.get(position).getUnits(), mobiles.get(position).getPrice()));
            holder.itemVendor.setText(mContext.getResources().getString(R.string.vendor_name, mobiles.get(position).getVendor()));
        }
    }

    public Mobile getItemAt(int position) {
        return mobiles != null && mobiles.size() > position ? mobiles.get(position) : null;
    }

    @Override
    public int getItemCount() {
        return mobiles != null ? mobiles.size() : 0;
    }

    class ViewHolderList extends RecyclerView.ViewHolder {
        final AppCompatImageView itemImage;
        final AppCompatTextView itemName;
        final AppCompatTextView itemVendor;
        final AppCompatTextView itemPrice;

        public ViewHolderList(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemImage = itemView.findViewById(R.id.item_image);
            itemVendor = itemView.findViewById(R.id.item_vendor);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemView.setOnClickListener(view -> {
                if(itemClickListener != null) {
                    itemClickListener.onItemClicked(ViewHolderList.this, getAdapterPosition());
                }
            });
        }
    }

    class ViewHolderGrid extends RecyclerView.ViewHolder {
        final AppCompatImageView itemImage;
        final AppCompatTextView itemName;
        final AppCompatTextView itemVendor;
        final AppCompatTextView itemPrice;

        public ViewHolderGrid(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemImage = itemView.findViewById(R.id.item_image);
            itemVendor = itemView.findViewById(R.id.item_vendor);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemView.setOnClickListener(view -> {
                if(itemClickListener != null) {
                    itemClickListener.onItemClicked(ViewHolderGrid.this, getAdapterPosition());
                }
            });
        }
    }
}
