package com.vwo.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vwo.sampleapp.R;
import com.vwo.sampleapp.adapters.AdapterSorting;
import com.vwo.sampleapp.data.MobileViewModel;
import com.vwo.sampleapp.interfaces.ChangeFragment;
import com.vwo.sampleapp.interfaces.ItemClickListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by aman on 07/08/17.
 */

public class FragmentSorting extends Fragment implements ItemClickListener {

    public static final String ARG_ITEM = "item";
    private static final String ARG_FRAGMENT_TYPE = "fragment_type";

    private int type;

    private AdapterSorting adapterSortingList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sorting, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.sorting_recycler_view);


        if (savedInstanceState == null) {
            assert getArguments() != null;
            type = getArguments().getInt(ARG_FRAGMENT_TYPE, FragmentSortingMain.ID_LIST_VARIATION);
        } else {
            type = savedInstanceState.getInt(ARG_FRAGMENT_TYPE, FragmentSortingMain.ID_LIST_VARIATION);
        }

        MobileViewModel mobileViewModel = ViewModelProviders.of(this).get(MobileViewModel.class);

        RecyclerView.LayoutManager layoutManager;
        if (type == FragmentSortingMain.ID_LIST_VARIATION) {
            layoutManager = new LinearLayoutManager(getContext());
        } else {
            layoutManager = new GridLayoutManager(getContext(), 2);
        }

        recyclerView.setLayoutManager(layoutManager);

        mobileViewModel.getMobiles().observe(this, mobiles -> {
            adapterSortingList = new AdapterSorting(new ArrayList<>(mobiles), getContext(), type, this);
            recyclerView.setAdapter(adapterSortingList);
        });

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
            listener.loadFragment(bundle, FragmentSortingMain.ID_DETAILS_VARIATION, FragmentSortingMain.TAG_VARIATION);
        }
    }
}
