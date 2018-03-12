package com.vwo.sampleapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vwo.sampleapp.R;
import com.vwo.sampleapp.adapters.AdapterSortingList;
import com.vwo.sampleapp.interfaces.ChangeFragment;
import com.vwo.sampleapp.interfaces.ItemClickListener;
import com.vwo.sampleapp.models.Mobile;

import java.util.ArrayList;

/**
 * Created by aman on 07/08/17.
 */

public class FragmentSorting extends Fragment implements ItemClickListener {

    public static final String ARG_ITEM = "item";
    private static final String ARG_FRAGMENT_TYPE = "fragment_type";

    private int type;

    private AdapterSortingList adapterSortingList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sorting, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.sorting_recycler_view);

        ArrayList<Mobile> mobiles = new ArrayList<>();
        Mobile apple = new Mobile("iPhone 6 (16GB, Black)", 399, "$", true,
                true, R.drawable.iphone, "Apple", "Also Available in Space Grey and Rose Gold", 4);
        Mobile samsung = new Mobile("Samsung Galaxy S8 (64GB, Midnight Black)", 799, "$",
                true, false, R.drawable.s8, "Samsung", "Also available in Maple Gold and Orchid Grey", 4);
        Mobile pixel = new Mobile("Google Pixel (32GB, Very Silver)", 699, "$", false,
                false, R.drawable.pixel, "Google", "Also Available in Quite black", 5);
        Mobile ZTE = new Mobile("ZTE Max XL (16GB)", 699, "$", true, false,
                R.drawable.zte, "ZTE", "Available in 16GB", 3);

        mobiles.add(apple);
        mobiles.add(pixel);
        mobiles.add(samsung);
        mobiles.add(ZTE);

        if (savedInstanceState == null) {
            assert getArguments() != null;
            type = getArguments().getInt(ARG_FRAGMENT_TYPE, FragmentSortingMain.ID_LIST_CONTROL);
        } else {
            type = savedInstanceState.getInt(ARG_FRAGMENT_TYPE, FragmentSortingMain.ID_LIST_CONTROL);
        }

        RecyclerView.LayoutManager layoutManager;
        if (type == FragmentSortingMain.ID_LIST_CONTROL || type == FragmentSortingMain.ID_LIST_VARIATION) {
            layoutManager = new LinearLayoutManager(getContext());
        } else {
            layoutManager = new GridLayoutManager(getContext(), 2);
        }

        adapterSortingList = new AdapterSortingList(mobiles, getContext(), type, this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterSortingList);

        return view;
    }


    public static FragmentSorting getInstance(@FragmentSortingMain.FragmentType int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_FRAGMENT_TYPE, type);

        FragmentSorting fragmentSorting = new FragmentSorting();
        fragmentSorting.setArguments(bundle);

        return fragmentSorting;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_FRAGMENT_TYPE, type);
    }

    @Override
    public void onItemClicked(RecyclerView.ViewHolder viewHolder, int position) {
        if (getParentFragment() instanceof ChangeFragment) {
            ChangeFragment listener = (ChangeFragment) getParentFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(ARG_ITEM, adapterSortingList.getItemAt(position));
            if (type == FragmentSortingMain.ID_LIST_CONTROL) {
                listener.loadFragment(bundle, FragmentSortingMain.ID_DETAILS_CONTROL, FragmentSortingMain.TAG_CONTROL);
            } else {
                listener.loadFragment(bundle, FragmentSortingMain.ID_DETAILS_VARIATION, FragmentSortingMain.TAG_VARIATION);
            }
        }
    }
}
