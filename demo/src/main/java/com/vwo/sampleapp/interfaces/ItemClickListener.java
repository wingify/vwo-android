package com.vwo.sampleapp.interfaces;

import android.support.v7.widget.RecyclerView;

/**
 * Created by aman on 08/08/17.
 */

public interface ItemClickListener {
    void onItemClicked(RecyclerView.ViewHolder viewHolder, int position);
}
