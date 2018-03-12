package com.vwo.sampleapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vwo.mobile.VWO;
import com.vwo.sampleapp.R;
import com.vwo.sampleapp.interfaces.ChangeFragment;
import com.vwo.sampleapp.interfaces.NavigationToggleListener;
import com.vwo.sampleapp.models.Mobile;
import com.vwo.sampleapp.utils.Constants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by aman on 07/08/17.
 */

public class FragmentSortingMain extends Fragment implements ChangeFragment {
    private static final String LOG_TAG = FragmentSortingMain.class.getSimpleName();

    private AppCompatTextView titleControl;
    private AppCompatTextView titleVariation;
    private NavigationToggleListener listener;

    public static final String TAG_CONTROL = "Control";
    public static final String TAG_VARIATION = "Variation";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ID_LIST_CONTROL,
            ID_DETAILS_CONTROL,
            ID_LIST_VARIATION,
            ID_GRID_VARIATION,
            ID_DETAILS_VARIATION})
    public @interface FragmentType{}

    public static final int ID_LIST_CONTROL = 0;
    public static final int ID_DETAILS_CONTROL = 1;
    public static final int ID_LIST_VARIATION = 2;
    public static final int ID_GRID_VARIATION = 3;
    public static final int ID_DETAILS_VARIATION = 4;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof NavigationToggleListener) {
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
        titleControl = view.findViewById(R.id.control_text_view_title);
        titleVariation = view.findViewById(R.id.variation_text_view_title);
        AppCompatTextView toolbarTitle = view.findViewById(R.id.toolbar_title);

        toolbarTitle.setText(R.string.title_layout_campaign);

        navigation.setOnClickListener(view1 -> {
            if(listener != null) {
                listener.onToggle();
            }
        });

        loadFragments();

        refresh.setOnClickListener(view12 -> loadFragments());

        return view;
    }

    private void loadFragments() {
        String value = String.valueOf(VWO.getVariationForKey(Constants.VWOKeys.KEY_LAYOUT, Constants.VWOKeys.VALUE_LIST));
        switch (value) {
            case Constants.VWOKeys.VALUE_LIST:
                loadFragment(null, ID_LIST_VARIATION, null);
                break;
            case Constants.VWOKeys.VALUE_GRID:
                loadFragment(null, ID_GRID_VARIATION, null);
                break;
            default:
                loadFragment(null, ID_LIST_VARIATION, null);
                break;
        }
        loadFragment(null, ID_LIST_CONTROL, null);
    }

    /**
     * <b> This function is used to load a particular {@link android.app.Fragment} from the
     * controlling {@link Activity} or {@link android.app.Fragment} </b>
     *
     * @param bundle     is the data to be passed to {@link android.app.Fragment}
     * @param fragmentId is the id that identifies, which {@link android.app.Fragment} is to be loaded
     * @param tag        is the tag that is attached to {@link android.app.Fragment} which is to be loaded
     */
    @Override
    public void loadFragment(@Nullable Bundle bundle, int fragmentId, @Nullable String tag) {
        switch (fragmentId) {
            case ID_LIST_CONTROL:
                Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
                if(fragment == null) {
                    titleControl.setText(getString(R.string.str_list_view));
                    getChildFragmentManager().beginTransaction().replace(R.id.sorting_control_container,
                            FragmentSorting.getInstance(fragmentId), TAG_CONTROL).commit();
                } else {
                    getChildFragmentManager().beginTransaction().replace(R.id.sorting_control_container,
                            fragment, tag).commit();
                }
                break;
            case ID_DETAILS_CONTROL:
                if(bundle != null) {
                    FragmentItemDetails detailsFragment = FragmentItemDetails.getInstance(bundle.getParcelable(FragmentSorting.ARG_ITEM), fragmentId);
                    getChildFragmentManager().beginTransaction().replace(R.id.sorting_control_container,
                            detailsFragment, null).addToBackStack(null).commit();
                }
                break;
            case ID_LIST_VARIATION:
                fragment = getChildFragmentManager().findFragmentByTag(tag);
                if(fragment == null) {
                    titleVariation.setText(getString(R.string.str_list_view));
                    getChildFragmentManager().beginTransaction().replace(R.id.sorting_variation_container,
                            FragmentSorting.getInstance(fragmentId), TAG_VARIATION).commit();
                } else {
                    getChildFragmentManager().beginTransaction().replace(R.id.sorting_variation_container,
                            fragment, tag).commit();
                }
                break;
            case ID_GRID_VARIATION:
                fragment = getChildFragmentManager().findFragmentByTag(tag);
                if(fragment == null) {
                    titleVariation.setText(getString(R.string.str_grid_view));
                    getChildFragmentManager().beginTransaction().replace(R.id.sorting_variation_container,
                            FragmentSorting.getInstance(fragmentId), TAG_VARIATION).commit();
                } else {
                    getChildFragmentManager().beginTransaction().replace(R.id.sorting_variation_container,
                            fragment, tag).commit();
                }
                break;
            case ID_DETAILS_VARIATION:
                if(bundle != null) {
                    FragmentItemDetails detailsFragment = FragmentItemDetails.getInstance(bundle.getParcelable(FragmentSorting.ARG_ITEM), fragmentId);
                    getChildFragmentManager().beginTransaction().replace(R.id.sorting_variation_container,
                            detailsFragment, null).addToBackStack(null).commit();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown fragment id : " + fragmentId);
        }
    }
}
