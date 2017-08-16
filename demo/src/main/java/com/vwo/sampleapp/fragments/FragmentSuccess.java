package com.vwo.sampleapp.fragments;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vwo.mobile.VWO;
import com.vwo.sampleapp.R;
import com.vwo.sampleapp.databinding.FragmentSuccessBinding;
import com.vwo.sampleapp.models.Success;
import com.vwo.sampleapp.utils.Constants;

/**
 * Created by aman on 09/08/17.
 */

public class FragmentSuccess extends Fragment {
    public static final String ARG_SUCCESS = "success";

    private Success success;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSuccessBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_success, container, false);

        View view = binding.getRoot();

        if (savedInstanceState == null) {
            success = getArguments().getParcelable(ARG_SUCCESS);
        } else {
            success = savedInstanceState.getParcelable(ARG_SUCCESS);
        }

        binding.setSuccess(success);
        VWO.markConversionForGoal(Constants.VWOKeys.GOAL_LANDING_PAGE);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_SUCCESS, success);
    }

    @BindingAdapter("app:successImage")
    public static void setSuccessImage(AppCompatImageView imageView, int imageResource) {
        Drawable drawable = VectorDrawableCompat.create(imageView.getContext().getResources(),
                imageResource, imageView.getContext().getTheme());
        imageView.setImageDrawable(drawable);
    }

    public static FragmentSuccess getInstance(Success success) {
        FragmentSuccess fragmentSuccess = new FragmentSuccess();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_SUCCESS, success);
        fragmentSuccess.setArguments(bundle);

        return fragmentSuccess;
    }
}
