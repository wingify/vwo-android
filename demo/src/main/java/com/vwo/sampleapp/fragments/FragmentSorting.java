package com.vwo.sampleapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vwo.mobile.VWO;
import com.vwo.sampleapp.R;
import com.vwo.sampleapp.adapters.AdapterSorting;
import com.vwo.sampleapp.data.MobileViewModel;
import com.vwo.sampleapp.interfaces.ChangeFragment;
import com.vwo.sampleapp.interfaces.ItemClickListener;
import com.vwo.sampleapp.utils.Constants;

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

    private static final String LOG_TAG = FragmentSorting.class.getSimpleName();

    static final String ARG_ITEM = "item";
    private static final String ARG_FRAGMENT_TYPE = "fragment_type";

    private int type;

    private MobileViewModel mobileViewModel;

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

        mobileViewModel = ViewModelProviders.of(this).get(MobileViewModel.class);

        RecyclerView.LayoutManager layoutManager;
        if (type == FragmentSortingMain.ID_LIST_VARIATION) {
            layoutManager = new LinearLayoutManager(getContext());
        } else {
            layoutManager = new GridLayoutManager(getContext(), 2);
        }

        recyclerView.setLayoutManager(layoutManager);

        adapterSortingList = new AdapterSorting(null, getContext(), this);
        recyclerView.setAdapter(adapterSortingList);

        mobileViewModel.getMobiles().observe(this, mobiles -> {
            adapterSortingList.updateData(mobiles);
            layoutManager.smoothScrollToPosition(recyclerView, null, 0);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshVariation();
    }

    private void refreshVariation() {
        String variationName = VWO.getVariationNameForTestKey(Constants.VWOKeys.TEST_KEY_SORTING);

        if (variationName != null) {
            Log.d(LOG_TAG, "Received variation: " + variationName);
            switch (variationName) {
                case Constants.VWOKeys.TEST_KEY_VALUE_SORT_BY_NAME:
                    mobileViewModel.sortByName();
                    break;
                case Constants.VWOKeys.TEST_KEY_VALUE_SORT_BY_PRICE:
                    mobileViewModel.sortByPrice();
                    break;
                default:
                    mobileViewModel.sortById();
                    break;
            }
        } else {
            mobileViewModel.sortById();
        }
    }

    static FragmentSorting getInstance(@FragmentSortingMain.FragmentType int type) {
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

    public void onRefreshClicked() {
        refreshVariation();
    }
}
