package com.vwo.sampleapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vwo.sampleapp.R;
import com.vwo.sampleapp.interfaces.ChangeFragment;
import com.vwo.sampleapp.interfaces.NavigationToggleListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

/**
 * Created by aman on 07/08/17.
 */

public class FragmentSortingMain extends Fragment implements ChangeFragment {
    private static final String LOG_TAG = FragmentSortingMain.class.getSimpleName();

    private NavigationToggleListener listener;

    public static final String TAG_CONTROL = "Control";
    public static final String TAG_VARIATION = "Variation";

    public static final String CURRENT_FRAGMENT_ID = "current_fragment_id";

    public void refreshChildFragments() {
        if(currentFragmentID == ID_LIST_VARIATION) {
            FragmentSorting fragment = (FragmentSorting) getChildFragmentManager().findFragmentByTag(TAG_VARIATION);
            if(fragment != null && fragment.isVisible()) {
                fragment.onRefreshClicked();
            } else {
                loadFragments();
            }
        } else {
            loadFragments();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            ID_LIST_VARIATION,
            ID_DETAILS_VARIATION})
    public @interface FragmentType {
    }

    public static final int ID_LIST_VARIATION = 1;
    public static final int ID_DETAILS_VARIATION = 2;

    private int currentFragmentID = -1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof NavigationToggleListener) {
            listener = (NavigationToggleListener) context;
        } else {
            Log.e(LOG_TAG, "Interface NavigationToggleListener not implemented in Activity");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sorting_main, container, false);
        setHasOptionsMenu(true);

        AppCompatImageView navigation = view.findViewById(R.id.campaign_navigation);
        AppCompatImageView refresh = view.findViewById(R.id.refresh_campaign);
        AppCompatTextView toolbarTitle = view.findViewById(R.id.toolbar_title);

        toolbarTitle.setText(R.string.title_layout_campaign);

        if(savedInstanceState != null) {
            currentFragmentID = savedInstanceState.getInt(CURRENT_FRAGMENT_ID, ID_LIST_VARIATION);
        } else {
            currentFragmentID = ID_LIST_VARIATION;
        }

        navigation.setOnClickListener(view1 -> {
            if (listener != null) {
                listener.onToggle();
            }
        });

        loadFragments();

        refresh.setOnClickListener(view12 -> {
            refreshChildFragments();
        });

        return view;
    }

    private void loadFragments() {
        loadFragment(null, currentFragmentID, null);
    }

    /**
     * <b> This function is used to load a particular {@link Fragment} from the
     * controlling {@link Activity} or {@link .Fragment} </b>
     *
     * @param bundle     is the data to be passed to {@link Fragment}
     * @param fragmentId is the id that identifies, which {@link Fragment} is to be loaded
     * @param tag        is the tag that is attached to {@link Fragment} which is to be loaded
     */
    @Override
    public void loadFragment(@Nullable Bundle bundle, int fragmentId, @Nullable String tag) {
        switch (fragmentId) {
            case ID_LIST_VARIATION:
                Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    getChildFragmentManager().beginTransaction().replace(R.id.sorting_variation_container,
                            FragmentSorting.getInstance(fragmentId), TAG_VARIATION).commit();
                } else {
                    getChildFragmentManager().beginTransaction().replace(R.id.sorting_variation_container,
                            fragment, tag).commit();
                }
                currentFragmentID = fragmentId;
                break;
            case ID_DETAILS_VARIATION:
                if (bundle != null) {
                    FragmentItemDetails detailsFragment = FragmentItemDetails.getInstance(bundle.getParcelable(FragmentSorting.ARG_ITEM), fragmentId);
                    getChildFragmentManager().beginTransaction().replace(R.id.sorting_variation_container,
                            detailsFragment, null).addToBackStack(null).commit();
                }
                currentFragmentID = fragmentId;
                break;
            default:
                throw new IllegalArgumentException("Unknown fragment id : " + fragmentId);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_FRAGMENT_ID, currentFragmentID);
        super.onSaveInstanceState(outState);
    }
}
