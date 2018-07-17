package com.vwo.sampleapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vwo.mobile.VWO;
import com.vwo.sampleapp.R;
import com.vwo.sampleapp.interfaces.ChangeFragment;
import com.vwo.sampleapp.interfaces.NavigationToggleListener;
import com.vwo.sampleapp.utils.Constants;

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

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            ID_LIST_VARIATION,
            ID_DETAILS_VARIATION})
    public @interface FragmentType{}

    public static final int ID_LIST_VARIATION = 1;
    public static final int ID_DETAILS_VARIATION = 2;

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
        String value = String.valueOf(VWO.getStringForKey(Constants.VWOKeys.KEY_LAYOUT, Constants.VWOKeys.VALUE_LIST));
        switch (value) {
            case Constants.VWOKeys.VALUE_LIST:
                loadFragment(null, ID_LIST_VARIATION, null);
                break;
            default:
                loadFragment(null, ID_LIST_VARIATION, null);
                break;
        }
        loadFragment(null, ID_LIST_VARIATION, null);
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
            case ID_LIST_VARIATION:
                Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
                if(fragment == null) {
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
